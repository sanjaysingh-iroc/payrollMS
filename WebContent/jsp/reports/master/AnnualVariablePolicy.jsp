<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript">
 function isNumberKey(evt){
    var charCode = (evt.which) ? evt.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
       return false;
    }
    return true;
 }
 
 function editPolicy(empId,type) {
	if(type == 'U') {
		document.getElementById("policyName_"+empId).style.display='none';
		document.getElementById("policyCombo_"+empId).style.display='inline';
	} else {
		document.getElementById("policyName_"+empId).style.display='inline';
		document.getElementById("policyCombo_"+empId).style.display='none';
	}
}

function updatePolicy(empId) {
	var amount = document.getElementById("policy_"+empId).value;
	var strOrg = document.getElementById("strOrg").value;
	var financialYear = document.getElementById("financialYear").value;
	var strLevel = document.getElementById("strLevel").value;
	var strSalaryHeadId = document.getElementById("strSalaryHeadId").value;
	 
	document.getElementById("policyName_"+empId).style.display='inline';
	document.getElementById("policyCombo_"+empId).style.display='none';
	
	var action = 'UpdateAnnualVariablePolicy.action?levelId='+strLevel+'&salaryHeadId='+strSalaryHeadId+'&amount='+amount;
	action += '&strOrg='+strOrg+'&financialYear='+financialYear+'&empId='+empId;
	getContent('policyName_'+empId, action);
}

function submitForm(type) {
	var strOrg = document.getElementById("strOrg").value;
	var financialYear = document.getElementById("financialYear").value;
	var action = 'AnnualVariablePolicy.action?strOrg='+strOrg+'&financialYear='+financialYear;
	if(type == '2'){
		var strLevel = document.getElementById("strLevel").value;
		action +='&strLevel='+strLevel;
	} else if(type == '3'){
		var strLevel = document.getElementById("strLevel").value;
		var strSalaryHeadId = document.getElementById("strSalaryHeadId").value;
		action +='&strLevel='+strLevel+'&strSalaryHeadId='+strSalaryHeadId;
	}
	//alert(action);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: action,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

function importAnnualVariable(){
	//financialYear strOrg strLevel strSalaryHeadId 
 	var financialYear = document.getElementById("financialYear").value;
 	var strOrg = document.getElementById("strOrg").value;
 	var strLevel = document.getElementById("strLevel").value;
 	var strSalaryHeadId = document.getElementById("strSalaryHeadId").value;
 	
 	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Import Annual Variable');
	 $.ajax({
			url : "ImportAnnualVariable.action?callFrom=AVP&financialYear="+financialYear+"&strOrg="+strOrg+"&strLevel="+strLevel+"&strSalaryHeadId="+strSalaryHeadId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}
 
</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmSalaryHeadAmount = (Map<String, String>) request.getAttribute("hmSalaryHeadAmount");
	if (hmSalaryHeadAmount == null) hmSalaryHeadAmount = new HashMap<String, String>();
	List<Map<String, String>> alEmp = (List<Map<String, String>>) request.getAttribute("alEmp");
	if (alEmp == null) alEmp = new ArrayList<Map<String, String>>();

	String strOrg = (String) request.getAttribute("strOrg");
	String strLevel = (String) request.getAttribute("strLevel");
	String strSalaryHeadId = (String) request.getAttribute("strSalaryHeadId");
%>

<section class="content">
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-none">
		<div class="box-body" style="min-height: 600px;">
			<div class="box box-default collapsed-box">
				<div class="box-header with-border">
					<h3 class="box-title"><%=(String) request.getAttribute("selectedFilter")%></h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<div class="box-body" style="padding: 5px; overflow-y: auto;">
					<s:form name="frm" action="AnnualVariablePolicy" theme="simple">
						<s:hidden name="userscreen" id="userscreen" />
						<s:hidden name="navigationId" id="navigationId" />
						<s:hidden name="toPage" id="toPage" />

						<div style="float: left; width: 99%; margin-left: 10px;">
							<div style="float: left; margin-right: 5px;">
								<i class="fa fa-filter"></i>
							</div>
							<div style="float: left; width: 98%;">
								<div style="float: left; margin-left: 10px;">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select name="financialYear" id="financialYear" list="financialYearList" listKey="financialYearId"
										listValue="financialYearName" onchange="submitForm('3');" />
								</div>
								<div style="float: left; margin-left: 10px;">
									<p style="padding-left: 5px;">Organisation</p>
									<s:select name="strOrg" id="strOrg" list="orgList" listKey="orgId" listValue="orgName"
										onchange="submitForm('1');" />
								</div>
								<div style="float: left; margin-left: 10px;">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="strLevel" id="strLevel" listKey="levelId" listValue="levelCodeName" headerKey=""
										headerValue="Choose Level" onchange="submitForm('2');" list="levelList" key="" required="true" />
								</div>
								<div style="float: left; margin-left: 10px;">
									<p style="padding-left: 5px;">Salary Head</p>
									<s:select theme="simple" name="strSalaryHeadId" id="strSalaryHeadId" listKey="salaryHeadId" listValue="salaryHeadName"
										headerKey="" headerValue="Choose Salary Head" list="salaryHeadList" key="" required="true" onchange="submitForm('3');" />
								</div>
							</div>
						</div>
					</s:form>
				</div>
			</div>

			<%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "")%>
			<%
				session.setAttribute("MESSAGE", "");
			%>

			<%
				if (uF.parseToInt(strSalaryHeadId) > 0) {
			%>
			<div style="text-align: right;">
				<a href="javascript:void(0)" onclick="importAnnualVariable();">Import Annual Variable</a>
			</div>
			<%
				}
			%>
			<div class="col-md-12">
				<ul class="level_list">
					<%
						int alSize = alEmp.size();
						for (int i = 0; i < alSize; i++) {
							Map<String, String> hmEmp = (Map<String, String>) alEmp.get(i);
							if (hmEmp == null) hmEmp = new HashMap<String, String>();
							double variableAmt = uF.parseToDouble(hmSalaryHeadAmount.get(hmEmp.get("EMP_ID")));
					%>
						<li style="float: left; border-bottom: 1px solid #CCCCCC; width: 100%;">
							<div style="width: 100%; float: left;">
								<div style="float: left; margin-right: 5px;">
									<a href="javascript:void(0);" onclick="editPolicy('<%=hmEmp.get("EMP_ID")%>', 'U')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
								</div>
								<div style="float: left; width: 31%;">
									<strong>Employee Name:</strong>&nbsp;<%=uF.showData(hmEmp.get("EMP_NAME"), "")%></div>
								<div style="float: left;">
									<strong>Amount:</strong>&nbsp; <span id="policyName_<%=hmEmp.get("EMP_ID")%>"><%=variableAmt%></span>
									<span style="display: none;" id="policyCombo_<%=hmEmp.get("EMP_ID")%>"> 
									<input type="text" name="policy_<%=hmEmp.get("EMP_ID")%>" id="policy_<%=hmEmp.get("EMP_ID")%>" style="width: 75px !important; text-align: right;" onkeypress="return isNumberKey(event)" value="<%=variableAmt%>" /> &nbsp;
									<input type="button" class="btn btn-primary" name="update_<%=hmEmp.get("EMP_ID")%>" id="update_<%=hmEmp.get("EMP_ID")%>" value="Save" onclick="updatePolicy('<%=hmEmp.get("EMP_ID")%>');">&nbsp;
									<input type="button" class="btn btn-default" name="cancel_<%=hmEmp.get("EMP_ID")%>" id="cancel_<%=hmEmp.get("EMP_ID")%>" value="Cancel" onclick="editPolicy('<%=hmEmp.get("EMP_ID")%>', 'C');">
									</span>
								</div>
							</div>
						</li>
					<%
						}
					%>

					<%
						if (uF.parseToInt(strSalaryHeadId) == 0) {
					%>
						<li><div class="msg nodata"><span>Please choose salary head.</span></div></li>
					<%
						} else if (alEmp.size() == 0) {
					%>
						<li><div class="msg nodata"><span>Please assign salary head to employee.</span></div></li>
					<%
						}
					%>

				</ul>
			</div>
		</div>
	</div>
	</section>
</div>
</section>

<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">-</h4>
			</div>
			<div class="modal-body"
				style="height: 200px; overflow-y: auto; padding-left: 25px;">
			</div>
			<div class="modal-footer">
				<button type="button" id="closeButton" class="btn btn-default"
					data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>