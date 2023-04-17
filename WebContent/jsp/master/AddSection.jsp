<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillUnderSection"%>
<%@page import="java.util.List"%>
<%@ page import="com.konnect.jpms.select.FillAmountType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>
	$( function () {
		$("#btnAddNewRowOk").click(function(){
			$('.validateRequired').filter(':hidden').prop('required',false);
    		$('.validateRequired').filter(':visible').prop('required',true);
			//$(".validateRequired").prop('required',true);
		});
	});

	$("#formAddNewRow").submit(function(event) {
		event.preventDefault();
		var financialYear = document.getElementById("financialYear").value;
		var form_data = $("#formAddNewRow").serialize();
		$.ajax({
			type : 'POST',
			url : 'AddSection.action',
			data : form_data,
			success : function(data) {
				$("#actionResult").html(data);
			},
			error: function(result) {
				$.ajax({
					url: 'SectionReport.action?financialYear='+financialYear,
					cache: true,
					success: function(result) {
						$("#actionResult").html(result);
			   		}
				});
			}
		});
	});

	
	function checkIsCeiling() {
    	if(document.getElementById("isCeilingApplicable").checked) {
    		document.getElementById("trCeiling").style.display = "table-row";
    	} else {
    		document.getElementById("sectionCeilingAmount").value = "";
    		document.getElementById("trCeiling").style.display = "none";
    	}
    }
	
	function checkIsCeilingApplicable(val) {
    	if(val == '%') {
    		//document.getElementById("trCeiling").style.display="table-row";
    		document.getElementById("trIsCeiling").style.display = "table-row";
    	} else {
			document.getElementById("sectionCeilingAmount").value = "";
			document.getElementById("isCeilingApplicable").checked = false;
			document.getElementById("trCeiling").style.display = "none";
			document.getElementById("trIsCeiling").style.display = "none";
    	}
    }
	
	
	function addSubSections() {
		if(document.getElementById("addSubsection").checked) {
    		document.getElementById("trAddSubsection0").style.display = "table-row";
    	} else {
    		document.getElementById("subSectionName0").value = "";
    		document.getElementById("subSectionDesc0").value = "";
    		document.getElementById("subSectionAmount0").value = "";
    		document.getElementById("trAddSubsection0").style.display = "none";
    		document.getElementById("subSectionName1").value = "";
    		document.getElementById("subSectionDesc1").value = "";
    		document.getElementById("subSectionAmount1").value = "";
    		document.getElementById("trAddSubsection1").style.display = "none";
    		document.getElementById("subSectionName2").value = "";
    		document.getElementById("subSectionDesc2").value = "";
    		document.getElementById("subSectionAmount2").value = "";
    		document.getElementById("trAddSubsection2").style.display = "none";
    		document.getElementById("subSectionName3").value = "";
    		document.getElementById("subSectionDesc3").value = "";
    		document.getElementById("subSectionAmount3").value = "";
    		document.getElementById("trAddSubsection3").style.display = "none";
    		document.getElementById("subSectionName4").value = "";
    		document.getElementById("subSectionDesc4").value = "";
    		document.getElementById("subSectionAmount4").value = "";
    		document.getElementById("trAddSubsection4").style.display = "none";
    	}
	}
	
	function addNewSubsection(trId) {
		document.getElementById(trId).style.display = "table-row";
	}
	
	
	function removeSubsection(trId, idCnt) {
		document.getElementById("subSectionName"+idCnt).value = "";
		document.getElementById("subSectionDesc"+idCnt).value = "";
		document.getElementById("subSectionAmount"+idCnt).value = "";
		document.getElementById(trId).style.display = "none";
	}
	
</script>
<% UtilityFunctions uF = new UtilityFunctions(); 
	List amountTypeList = (List) request.getAttribute("amountTypeList");
	String subSectionName0 = (String)request.getAttribute("subSectionName0");
	String subSectionName1 = (String)request.getAttribute("subSectionName1");
	String subSectionName2 = (String)request.getAttribute("subSectionName2");
	String subSectionName3 = (String)request.getAttribute("subSectionName3");
	String subSectionName4 = (String)request.getAttribute("subSectionName4");
	boolean addSubSec = false;
	if((subSectionName0 != null && subSectionName0.trim().length()>0) || (subSectionName1 != null && subSectionName1.trim().length()>0) || (subSectionName2 != null && subSectionName2.trim().length()>0) || 
		(subSectionName3 != null && subSectionName3.trim().length()>0) || (subSectionName4 != null && subSectionName4.trim().length()>0)) {
		addSubSec = true;
	}
	
%>
<s:form theme="simple" id="formAddNewRow" name="formAddNewRow" action="AddSection" method="POST">
	<s:hidden name="financialYear" id="financialYear"/>
	<s:hidden name="sectionId" />
	<s:hidden name="operation" />
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	
	<table border="0" class="table table_no_border form-table">
		<tr>
			<td class="txtlabel alignRight">Section Code:<sup>*</sup></td>
			<td><s:textfield name="sectionCode" id="sectionCode" cssClass="validateRequired"/>
				<span class="hint">Section Code<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
	
		<tr>
			<td class="txtlabel alignRight">Section Description:</td>
			<td><s:textfield name="sectionDesc" id="sectionDesc" />
				<span class="hint">Section Description<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Add Sub-section:</td>
			<td>
				<% if(addSubSec) { %>
				<s:checkbox name="addSubsection" id="addSubsection" fieldValue="true" value="true" onclick="addSubSections();" />
			<% } else { %>
				<s:checkbox name="addSubsection" id="addSubsection" onclick="addSubSections();" />
			<% } %>
			</td>
		</tr>
		
		<tr id="trAddSubsection0" style="display: <%=(subSectionName0 !=null && subSectionName0.trim().length()>0) ? "table-row" : "none" %>;">
			<td class="txtlabel alignRight"></td>
			<td class="txtlabel ">
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Name:<sup>*</sup></span> &nbsp; <s:textfield name="subSectionName0" id="subSectionName0" cssClass="validateRequired"/></div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Description:</span> &nbsp; <s:textfield name="subSectionDesc0" id="subSectionDesc0" /></div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Limit Type:<sup>*</sup></span> 
					<select name="subSectionLimitType0" id="subSectionLimitType0" style="margin-left: 10px !important;">
						<% for (int i=0; i<amountTypeList.size(); i++) { %>
						<option value=<%=((FillAmountType)amountTypeList.get(i)).getAmountTypeId() %>
						<% if(((FillAmountType)amountTypeList.get(i)).getAmountTypeId().equals((String)request.getAttribute("subSectionLimitType0"))) { %>
							selected
						<% } %>> <%=((FillAmountType)amountTypeList.get(i)).getAmountTypeName() %></option>
						<% } %>
					</select>
				</div>
				<div style="padding: 5px;"><span style="float:left; text-align:right; width:160px;">Sub Section Limit:<sup>*</sup></span> &nbsp; <s:textfield name="subSectionAmount0" id="subSectionAmount0" cssClass="validateRequired"/>
					<span style="text-align: right;">
						<a href="javascript:void(0)" onclick="addNewSubsection('trAddSubsection1')" class="fa fa-fw fa-plus" title="Add New Sub-section ">&nbsp;</a>
						<a href="javascript:void(0)" onclick="removeSubsection('trAddSubsection0', '0')" class="fa fa-fw fa-remove" title="Remove Sub-section">&nbsp;</a>
					</span>
				</div>
				<div style="padding: 5px;"><span style="float:left; text-align:right; width:160px;">Adjustable Gross Total Income Limit:</span> &nbsp; 
					<% boolean isAdjustGrossTotalIncomeLimitSubSec0 = (Boolean) request.getAttribute("isAdjustGrossTotalIncomeLimitSubSec0"); %>
					<% if(isAdjustGrossTotalIncomeLimitSubSec0) { %>
						<s:checkbox name="isAdjustGrossTotalIncomeLimitSubSec0" id="isAdjustGrossTotalIncomeLimitSubSec0" fieldValue="true" value="true"/>
					<% } else { %>
						<s:checkbox name="isAdjustGrossTotalIncomeLimitSubSec0" id="isAdjustGrossTotalIncomeLimitSubSec0"/>
					<% } %>
				</div>
			</td>
		</tr>
		
		<tr id="trAddSubsection1" style="display: <%=(subSectionName1 !=null && subSectionName1.trim().length()>0) ? "table-row" : "none" %>;">
			<td class="txtlabel alignRight"></td>
			<td class="txtlabel ">
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Name:<sup>*</sup></span> &nbsp; <s:textfield name="subSectionName1" id="subSectionName1" cssClass="validateRequired"/></div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Description:</span> &nbsp; <s:textfield name="subSectionDesc1" id="subSectionDesc1" /></div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Limit Type:<sup>*</sup></span> 
					<select name="subSectionLimitType1" id="subSectionLimitType1" style="margin-left: 10px !important;">
						<% for (int i=0; i<amountTypeList.size(); i++) { %>
						<option value=<%=((FillAmountType)amountTypeList.get(i)).getAmountTypeId() %>
						<% if(((FillAmountType)amountTypeList.get(i)).getAmountTypeId().equals((String)request.getAttribute("subSectionLimitType1"))) { %>
							selected
						<% } %>> <%=((FillAmountType)amountTypeList.get(i)).getAmountTypeName() %></option>
						<% } %>
					</select>
				</div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Limit:<sup>*</sup></span> &nbsp; <s:textfield name="subSectionAmount1" id="subSectionAmount1" cssClass="validateRequired"/></div>
				<div style="padding: 5px;"><span style="float:left; text-align:right; width:160px;">Adjustable Gross Total Income Limit:</span> &nbsp; 
					<% boolean isAdjustGrossTotalIncomeLimitSubSec1 = (Boolean) request.getAttribute("isAdjustGrossTotalIncomeLimitSubSec1"); %>
					<% if(isAdjustGrossTotalIncomeLimitSubSec1) { %>
						<s:checkbox name="isAdjustGrossTotalIncomeLimitSubSec1" id="isAdjustGrossTotalIncomeLimitSubSec1" fieldValue="true" value="true"/>
					<% } else { %>
						<s:checkbox name="isAdjustGrossTotalIncomeLimitSubSec1" id="isAdjustGrossTotalIncomeLimitSubSec1"/>
					<% } %>
					<span style="text-align:right;">
						<a href="javascript:void(0)" onclick="addNewSubsection('trAddSubsection2')" class="fa fa-fw fa-plus" title="Add New Sub-section ">&nbsp;</a>
						<a href="javascript:void(0)" onclick="removeSubsection('trAddSubsection1', '1')" class="fa fa-fw fa-remove" title="Remove Sub-section">&nbsp;</a>
					</span>
				</div>
			</td>
		</tr>
		
		<tr id="trAddSubsection2" style="display: <%=(subSectionName2 !=null && subSectionName2.trim().length()>0) ? "table-row" : "none" %>;">
			<td class="txtlabel alignRight"></td>
			<td class="txtlabel ">
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Name:<sup>*</sup></span> &nbsp; <s:textfield name="subSectionName2" id="subSectionName2" cssClass="validateRequired"/></div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Description:</span> &nbsp; <s:textfield name="subSectionDesc2" id="subSectionDesc2" /></div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Limit Type:<sup>*</sup></span> 
					<select name="subSectionLimitType2" id="subSectionLimitType2" style="margin-left: 10px !important;">
						<% for (int i=0; i<amountTypeList.size(); i++) { %>
						<option value=<%=((FillAmountType)amountTypeList.get(i)).getAmountTypeId() %>
						<% if(((FillAmountType)amountTypeList.get(i)).getAmountTypeId().equals((String)request.getAttribute("subSectionLimitType2"))) { %>
							selected
						<% } %>> <%=((FillAmountType)amountTypeList.get(i)).getAmountTypeName() %></option>
						<% } %>
					</select>
				</div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Limit:<sup>*</sup></span> &nbsp; <s:textfield name="subSectionAmount2" id="subSectionAmount2" cssClass="validateRequired"/></div>
				<div style="padding: 5px;"><span style="float:left; text-align:right; width:160px;">Adjustable Gross Total Income Limit:</span> &nbsp; 
					<% boolean isAdjustGrossTotalIncomeLimitSubSec2 = (Boolean) request.getAttribute("isAdjustGrossTotalIncomeLimitSubSec2"); %>
					<% if(isAdjustGrossTotalIncomeLimitSubSec2) { %>
						<s:checkbox name="isAdjustGrossTotalIncomeLimitSubSec2" id="isAdjustGrossTotalIncomeLimitSubSec2" fieldValue="true" value="true"/>
					<% } else { %>
						<s:checkbox name="isAdjustGrossTotalIncomeLimitSubSec2" id="isAdjustGrossTotalIncomeLimitSubSec2"/>
					<% } %>
					<span style="text-align: right;">
						<a href="javascript:void(0)" onclick="addNewSubsection('trAddSubsection3')" class="fa fa-fw fa-plus" title="Add New Sub-section ">&nbsp;</a>
						<a href="javascript:void(0)" onclick="removeSubsection('trAddSubsection2','2')" class="fa fa-fw fa-remove" title="Remove Sub-section">&nbsp;</a>
					</span>
				</div>
			</td>
		</tr>
		
		<tr id="trAddSubsection3" style="display: <%=(subSectionName3 !=null && subSectionName3.trim().length()>0) ? "table-row" : "none" %>;">
			<td class="txtlabel alignRight"></td>
			<td class="txtlabel ">
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Name:<sup>*</sup></span> &nbsp; <s:textfield name="subSectionName3" id="subSectionName3" cssClass="validateRequired"/></div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Description:</span> &nbsp; <s:textfield name="subSectionDesc3" id="subSectionDesc3" /></div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Limit Type:<sup>*</sup></span> 
					<select name="subSectionLimitType3" id="subSectionLimitType3" style="margin-left: 10px !important;">
						<% for (int i=0; i<amountTypeList.size(); i++) { %>
						<option value=<%=((FillAmountType)amountTypeList.get(i)).getAmountTypeId() %>
						<% if(((FillAmountType)amountTypeList.get(i)).getAmountTypeId().equals((String)request.getAttribute("subSectionLimitType3"))) { %>
							selected
						<% } %>> <%=((FillAmountType)amountTypeList.get(i)).getAmountTypeName() %></option>
						<% } %>
					</select>
				</div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Limit:<sup>*</sup></span> &nbsp; <s:textfield name="subSectionAmount3" id="subSectionAmount3" cssClass="validateRequired"/></div>
				<div style="padding: 5px;"><span style="float:left; text-align:right; width:160px;">Adjustable Gross Total Income Limit:</span> &nbsp; 
					<% boolean isAdjustGrossTotalIncomeLimitSubSec3 = (Boolean) request.getAttribute("isAdjustGrossTotalIncomeLimitSubSec3"); %>
					<% if(isAdjustGrossTotalIncomeLimitSubSec3) { %>
						<s:checkbox name="isAdjustGrossTotalIncomeLimitSubSec3" id="isAdjustGrossTotalIncomeLimitSubSec3" fieldValue="true" value="true"/>
					<% } else { %>
						<s:checkbox name="isAdjustGrossTotalIncomeLimitSubSec3" id="isAdjustGrossTotalIncomeLimitSubSec3"/>
					<% } %>
					<span style="text-align: right;">
						<a href="javascript:void(0)" onclick="addNewSubsection('trAddSubsection4')" class="fa fa-fw fa-plus" title="Add New Sub-section ">&nbsp;</a>
						<a href="javascript:void(0)" onclick="removeSubsection('trAddSubsection3','3')" class="fa fa-fw fa-remove" title="Remove Sub-section">&nbsp;</a>
					</span>
				</div>
			</td>
		</tr>
		
		<tr id="trAddSubsection4" style="display: <%=(subSectionName4 !=null && subSectionName4.trim().length()>0) ? "table-row" : "none" %>;">
			<td class="txtlabel alignRight"></td>
			<td class="txtlabel ">
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Name:<sup>*</sup></span> &nbsp; <s:textfield name="subSectionName4" id="subSectionName4" cssClass="validateRequired"/></div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Description:</span> &nbsp; <s:textfield name="subSectionDesc4" id="subSectionDesc4" /></div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Limit Type:<sup>*</sup></span> 
					<select name="subSectionLimitType4" id="subSectionLimitType4" style="margin-left: 10px !important;">
						<% for (int i=0; i<amountTypeList.size(); i++) { %>
						<option value=<%=((FillAmountType)amountTypeList.get(i)).getAmountTypeId() %>
						<% if(((FillAmountType)amountTypeList.get(i)).getAmountTypeId().equals((String)request.getAttribute("subSectionLimitType4"))) { %>
							selected
						<% } %>> <%=((FillAmountType)amountTypeList.get(i)).getAmountTypeName() %></option>
						<% } %>
					</select>
				</div>
				<div style="padding: 5px;"><span style="float:left; text-align: right; width: 160px;">Sub Section Limit:<sup>*</sup></span> &nbsp; <s:textfield name="subSectionAmount4" id="subSectionAmount4" cssClass="validateRequired"/></div>
				<div style="padding: 5px;"><span style="float:left; text-align:right; width:160px;">Adjustable Gross Total Income Limit:</span> &nbsp; 
					<% boolean isAdjustGrossTotalIncomeLimitSubSec4 = (Boolean) request.getAttribute("isAdjustGrossTotalIncomeLimitSubSec4"); %>
					<% if(isAdjustGrossTotalIncomeLimitSubSec4) { %>
						<s:checkbox name="isAdjustGrossTotalIncomeLimitSubSec4" id="isAdjustGrossTotalIncomeLimitSubSec4" fieldValue="true" value="true"/>
					<% } else { %>
						<s:checkbox name="isAdjustGrossTotalIncomeLimitSubSec4" id="isAdjustGrossTotalIncomeLimitSubSec4"/>
					<% } %>
					<span style="text-align: right;">
						<a href="javascript:void(0)" onclick="removeSubsection('trAddSubsection4', '4')" class="fa fa-fw fa-remove" title="Remove Sub-section">&nbsp;</a>
					</span>
				</div>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Section Limit Type:</td>
			<td>
				<% String sectionLimitType = (String) request.getAttribute("sectionLimitType"); %>
				<select name="sectionLimitType" id="sectionLimitType" onchange="checkIsCeilingApplicable(this.value);">
					<% for (int i=0; i<amountTypeList.size(); i++) { %>
					<option value=<%=((FillAmountType)amountTypeList.get(i)).getAmountTypeId() %>
					<% if(((FillAmountType)amountTypeList.get(i)).getAmountTypeId().equals(sectionLimitType)) { %>
						selected
					<% } %>> <%=((FillAmountType)amountTypeList.get(i)).getAmountTypeName() %></option>
					<% } %>
				</select>
				<span class="hint">Section Limit Type<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Section Exemption Limit:<sup>*</sup></td>
			<td><s:textfield name="sectionExemptionLimit" id="sectionExemptionLimit" cssClass="validateRequired" />
				<span class="hint">Section Exemption Limit<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		<tr id="trIsCeiling" style="display: <%=(sectionLimitType != null && sectionLimitType.equals("%")) ? "table-row" : "none" %>;">
			<td class="txtlabel alignRight">Is Ceiling applicable:</td>
			<td>
			<% boolean isCeilingApplicable = (Boolean) request.getAttribute("isCeilingApplicable"); %>
			<% if(isCeilingApplicable) { %>
				<s:checkbox name="isCeilingApplicable" id="isCeilingApplicable" fieldValue="true" value="true" onclick="checkIsCeiling();" />
			<% } else { %>
				<s:checkbox name="isCeilingApplicable" id="isCeilingApplicable" onclick="checkIsCeiling();" />
			<% } %>
			</td>
		</tr>
		
		<tr id="trCeiling" style="display: <%=isCeilingApplicable ? "table-row" : "none" %>;">
			<td class="txtlabel alignRight">Ceiling Amount:<sup>*</sup></td>
			<td><s:textfield name="sectionCeilingAmount" id="sectionCeilingAmount" cssClass="validateRequired" /></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Under Chapter:<sup>*</sup></td>
			<td>
				<select name="strUnderSection" id="strUnderSection" rel="4" class="validateRequired" >
					<!-- <option value="">Select Under Section</option> -->
					<% java.util.List underSectionList = (java.util.List) request.getAttribute("underSectionList"); %>
					<% for (int i=0; i<underSectionList.size(); i++) { %>
					<option value=<%=((FillUnderSection)underSectionList.get(i)).getUnderSectionId() %>
					<% if(uF.parseToInt(((FillUnderSection)underSectionList.get(i)).getUnderSectionId()) == uF.parseToInt((String)request.getAttribute("strUnderSection"))) { %>
						selected
					<% } %>> <%=((FillUnderSection)underSectionList.get(i)).getUnderSectionName() %></option>
					<% } %>
				</select>
				
				<span class="hint">Under Section<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Is PF applicable:</td>
			<td>
			<% boolean isPFApplicable = (Boolean) request.getAttribute("isPFApplicable"); %>
			<% if(isPFApplicable) { %>
				<s:checkbox name="isPFApplicable" id="isPFApplicable" fieldValue="true" value="true"/>
			<% } else { %>
				<s:checkbox name="isPFApplicable" id="isPFApplicable"/>
			<% } %>
			</td>
		</tr>
		
		<tr>
			<td class="alignRight">Select Slab Type:<sup>*</sup></td>
			<td>
				<select name="slabType" id="slabType" class="validateRequired">
					<option value="0" <%=(request.getAttribute("slabType") != null && ((String)request.getAttribute("slabType")).equals("0")) ? "selected" : "" %>>Standard</option>
					<option value="1" <%=(request.getAttribute("slabType") != null && ((String)request.getAttribute("slabType")).equals("1")) ? "selected" : "" %>>New</option>
				</select>
				<span class="hint">Select the slab type.<br>Standard Slab<br>New Slab<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td></td>
			<td>
				<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk" /> 
			</td>
		</tr>

	</table>
	
</s:form>

