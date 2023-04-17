<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<script type="text/javascript">

function checkUncheckValue() {
	var allEmp=document.getElementById("allEmp");		
	var strEmpId = document.getElementsByName('empIds');

	if(allEmp.checked==true){
		for(var i=0;i<strEmpId.length;i++){
			strEmpId[i].checked = true;
		}
	}else{		
		for(var i=0;i<strEmpId.length;i++){
			strEmpId[i].checked = false;			 
		}		 
	}
}


$("#formTDSUpdateChallanData").submit(function(e){
	e.preventDefault();
	var form_data = $("form[name='formTDSUpdateChallanData']").serialize();
   	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   	$.ajax({
   		type: 'POST',
		url : "TDSUpdateChallanData.action",
		data: form_data,
		cache : false,
		success : function(res) {
			$("#divResult").html(res);
		},
		error: function(result){
			$.ajax({
				url: 'TDSTaxChallan.action',
				cache: true,
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
		}
	});
});

</script>

	<%
		UtilityFunctions uF = new UtilityFunctions();
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
		Map hmEmpName = (Map)request.getAttribute("hmEmpName");
		Map<String,Map<String,String>> hmMap = (Map<String,Map<String,String>>)request.getAttribute("hmMap");
		List<String> empList=(List<String>)request.getAttribute("empList");
		String totalMonths=(String)request.getAttribute("months");
		String financialYear=(String)request.getAttribute("financialYear");
		String totalamountpaid=(String)request.getAttribute("totalamountpaid");
	%>

	<s:form id="formTDSUpdateChallanData" action="TDSUpdateChallanData" method="post" name="formTDSUpdateChallanData" theme="simple">
		<input type="hidden" name="financialYear" value="<%=financialYear%>"/>
		<input type="hidden" name="totalMonths" value="<%=totalMonths%>"/>
		<input type="hidden" name="operation" value="insert"/>		
		<s:hidden name="f_org"></s:hidden>
		<div style="float: center" id="tblDiv">
			<table border="0" class="table table-bordered">
				<tr>
					<td><strong>Print the TDS Challan</strong></td>
				</tr>
			</table>
			<!-- 
			Amount Unpaid -->
			<table border="0" class="table table-bordered">
				<tr>
					<th><input onclick="checkUncheckValue();" type="checkbox" name="allEmp" id="allEmp">Name</th>
					<th>Amount</th>
				</tr>
				<%if(empList!=null){
					for(int i=0;i<empList.size();i++) {
						Map<String,String> hmInner =hmMap.get(empList.get(i));
				%>
				<tr>
 					<td class="txtlabel alignLeft"><input type="checkbox" name="empIds" value="<%=empList.get(i) %>"/><%=uF.showData((String)hmEmpName.get(hmInner.get("EMP_ID")), "")%></td> 

					<td class="txtlabel alignRight"><%=hmInner.get("AMOUNT") %></td>
				</tr>
				<%}} %>
				<tr>
 					<td class="txtlabel alignLeft"><b>Total</b></td> 

					<td class="txtlabel alignRight"><b><%=uF.showData(totalamountpaid,"0") %></b></td>
				</tr>
				
			</table>
			<% if (empList == null || empList.size() == 0) { %>
			<div class="msg nodata"><span>No more unpaid amount.</span></div>
			<% } %>
		</div>
		
		<div class="clr"></div>
		<div style="margin: 0px 0px 0px 210px">
			<% if (empList != null && empList.size() > 0) { %>
				<table border="0" class="table table-bordered">
					<tr>
						<td class="txtlabel alignLeft"><s:submit value="Generate Challan" cssClass="btn btn-primary" name="strSubmit" /></td>
					</tr>
				</table>
			<% } %>
		</div>
	</s:form>

	