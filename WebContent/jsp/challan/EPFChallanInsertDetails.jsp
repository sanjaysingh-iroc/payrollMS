<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	Map hmEmpName = (Map) request.getAttribute("hmEmpName");
	Map<String, Map<String, String>> hmMap = (Map<String, Map<String, String>>) request.getAttribute("hmMap");
	List<String> empList = (List<String>) request.getAttribute("empList");
	String totalMonths = (String) request.getAttribute("months");
	String financialYear = (String) request.getAttribute("financialYear");
	String totalunpaidamount=(String)request.getAttribute("totalunpaidamount");
%>

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

$("#formEPFUpdateChallanData").submit(function(e){
	e.preventDefault();
	var form_data = $("form[name='formEPFUpdateChallanData']").serialize();
   	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   	$.ajax({
   		type: 'POST',
		url : "EPFUpdateChallanData.action",
		data: form_data,
		cache : false,
		success : function(res) {
			$("#divResult").html(res);
		},
		error: function(result){
			$.ajax({
				url: 'EPFChallan.action',
				cache: true,
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
		}
	});
});

</script>

	<s:form name="formEPFUpdateChallanData" id="formEPFUpdateChallanData" action="EPFUpdateChallanData" method="post" theme="simple">
		<input type="hidden" name="financialYear" value="<%=financialYear%>"/>
		<input type="hidden" name="totalMonths" value="<%=totalMonths%>"/>
		<input type="hidden" name="operation" value="insert"/>
		<s:hidden name="f_org"></s:hidden>
		
		<div style="float: center" id="tblDiv">
			<table border="0" class="table table-bordered">
				<tr>
					<td><strong>Print the challan</strong></td>
				</tr>
			</table>
			<!-- 
			Amount Unpaid -->
			<table border="0" class="table table-bordered">
				<tr>
					<th><input onclick="checkUncheckValue();" type="checkbox" name="allEmp" id="allEmp">Name</th>
					<th>Amount</th>
				</tr>
				<%
					int count = 0;
					if (empList != null) {
						for (int i = 0; i < empList.size(); i++) {
							count++;
							Map<String, String> hmInner = hmMap.get(empList.get(i));
				%>
				<tr>
 					<td class="txtlabel alignLeft"><input type="checkbox" name="empIds" value="<%=empList.get(i)%>"/><%=uF.showData((String) hmEmpName.get(hmInner.get("EMP_ID")), "")%></td> 
					<td class="txtlabel alignRight"><%=hmInner.get("AMOUNT")%></td>
				</tr>
				<% } %>
				<tr>
 					<td class="txtlabel alignLeft"><b>Total</b></td> 
					
					<td class="txtlabel alignRight"><b><%=uF.showData(uF.formatIntoComma(uF.parseToDouble(totalunpaidamount)), "0")%></b></td>
				</tr>
				<% } %>
				<% if (count == 0) { %>
				<tr>
					<td colspan="4"><div class="msg nodata">
							<span>No more challan available.</span>
						</div>
					</td>
				</tr>
				<% } %>
			</table>
		</div>
		
		<div class="clr"></div>
		<div style="margin: 0px 0px 0px 210px">
			<% if(count > 0) { %>
				<table border="0" class="table table-bordered">
					<tr>
						<td class="txtlabel alignLeft"><s:submit value="Generate Challan" cssClass="btn btn-primary" name="strSubmit"></s:submit>
						</td>
						<td></td>
					</tr>
				</table>
			<% } %>
		</div>
	</s:form>

