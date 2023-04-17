<%@page import="com.konnect.jpms.util.IMessages"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%--  <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>--%>

<style>
.table_no_border>thead>tr>th, .table_no_border>tbody>tr>th, .table_no_border>tfoot>tr>th, 
.table_no_border>thead>tr>td, .table_no_border>tbody>tr>td, .table_no_border>tfoot>tr>td {
border-top: 1px solid #FFFFFF;
}
</style>
<%
UtilityFunctions uF = new UtilityFunctions();
List alEPFSettings = (List)request.getAttribute("alEPFSettings");
if(alEPFSettings==null)alEPFSettings=new ArrayList();
Map hmEPFSettings = (Map)request.getAttribute("hmEPFSettings");
if(hmEPFSettings==null)hmEPFSettings=new HashMap();

String currency = (String)request.getAttribute("currency");

int isLevels = (Integer)request.getAttribute("isLevels");

%>

<script>
	
	$(function(){
		$(".salaryHeadName").multiselect().multiselectfilter();
		 <%-- var isLevels = '<%=(Integer)request.getAttribute("isLevels") %>';
		 if(isLevels == 0) {
			 document.getElementById("epfUpdate").style.display = 'none';
			 document.getElementById("strMessage").style.display = 'block';
		 } else {
			 document.getElementById("epfUpdate").style.display = 'block';
			 document.getElementById("strMessage").style.display = 'none';
		 } --%>
	});
	
	function getData(type) {
		var strOrg='';
		var strLevel='';
		/* var financialYear = document.getElementById("financialYear").value;
		var userscreen = document.getElementById("userscreen").value;
		var navigationId = document.getElementById("navigationId").value;
		var toPage = document.getElementById("toPage").value; */
		//alert("type ===>> " + type);
		if(type=='1') {
			/* strOrg=document.getElementById("strOrg").value; */
			document.getElementById("levelIdV").selectedIndex = -1;
		} else {
			/* strOrg=document.getElementById("strOrg").value; */		
		}
		var form_data = $("#frm_epfSetting").serialize();
		if(type == 3) {
			form_data = form_data + "&epfUpdate=Update";
		}
		//alert("form_data ===>> " + form_data);
		$.ajax({
			url : 'EPFSetting.action',
			data: form_data,
			cache: false,
			success:function(data) {
				//alert("data==>"+data);
				$("#actionResult").html(data);
			}
		});
		//window.location='MyDashboard.action?strOrg='+strOrg+'&strLevel='+strLevel+'&strCFYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
	}
	
</script> 
 

<div class="box-body">
	<s:form name="frm_epfSetting" id="frm_epfSetting" theme="simple" action="EPFSetting">
		<s:hidden name="userscreen" id="userscreen" />
		<s:hidden name="navigationId" id="navigationId" />
		<s:hidden name="toPage" id="toPage" />
		<input type="hidden" name="strCFYear" id="strCFYear" value="<%=(String)request.getAttribute("financialYear") %>" />
		<div class="box box-default collapsed-box">
			<div class="box-header with-border">
			    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			    <div class="box-tools pull-right">
			        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<div style="float: left; width: 99%; margin-left: 10px;">
					<div style="float: left; margin-right: 5px;">
						<i class="fa fa-filter"></i>
					</div>
					<div style="float: left;">
						<div style="float: left; margin-left: 10px;">
							<p style="padding-left: 5px;">Financial Year</p>
							<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="financialYear" 
							id="financialYear" onchange="getData('0');" cssClass="form-control autoWidth inline"/>
						</div>
						<div style="float: left; margin-left: 10px;">
							<p style="padding-left: 5px;">Organization</p>
							<s:select theme="simple" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" 
							onchange="getData('1');" list="orgList" key=""  cssClass="form-control autoWidth inline"/>
						</div>
						<div style="float: left; margin-left: 10px;">
							<p style="padding-left: 5px;">Level</p>
							<s:select name="strLevel" list="levelList" listKey="levelId" id="levelIdV" listValue="levelCodeName" 
							required="true" onchange="getData('2');" cssClass="form-control autoWidth inline"/>
						</div>	
					</div>
				</div>
			</div>
		</div>
			
		<div class="col-md-12">
			<p style="font-size:12px; padding-left:42px; padding-right:10px; font-style:italic; float:right;">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>
		</div>
		
		<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
		<% session.setAttribute("MESSAGE", ""); %>
	
		<table class="table table_no_border">
			<tr>	
				<td  colspan="2"><strong>Salary Heads to be considered</strong></td>
			</tr>
			<tr>	
				<td  colspan="2">
					<s:select list="salaryHeadList" listValue="salaryHeadName" listKey="salaryHeadId"
					 multiple="true" size="5" name="strSalaryHeadId" cssClass="salaryHeadName"></s:select>
				
				</td>
			</tr>
			
			<tr>	
				<td  colspan="2"><strong>% share under Statutory Compliance</strong></td>
			</tr>
			
			<tr>	
				<td  colspan="2"><strong>Employee Provident Fund</strong></td>
			</tr>
			<tr>	
				<td width="20%" class="alignRight">Employee's Contribution: </td>
				<td width="30%"><s:textfield name="eepfContribution" cssStyle="width:50px !important;text-align:right;" onkeypress="return isNumberKey(event)"></s:textfield> %</td>
			</tr>
			
			<tr>	
				<td class="alignRight">Max Limit: </td>
				<td ><s:textfield name="epfMaxLimit" cssStyle="width:50px !important;text-align:right;"  onkeypress="return isNumberKey(event)"></s:textfield>&nbsp;<%=uF.showData(currency, "") %></td>
			</tr>
			 
			<tr>	
				<td class="alignRight">Employer's Contribution: </td>
				<td >
				<s:textfield name="erpfContribution" cssStyle="width:50px !important;text-align:right;"  onkeypress="return isNumberKey(event)"></s:textfield> %
				<s:checkbox name="erpfContributionchbox" cssStyle="text-align:right;" ></s:checkbox> [Enable for employee deduction]
				</td>
			</tr>
			<tr>	
				<td class="alignRight">Max Limit: </td>
				<td ><s:textfield name="erpfMaxLimit" cssStyle="width:50px !important;text-align:right;"  onkeypress="return isNumberKey(event)"></s:textfield>&nbsp;<%=uF.showData(currency, "") %></td>
			</tr>
			<tr>	
				<td  colspan="2"><strong>Employee Pension Scheme</strong></td>
			</tr>
			<tr>	
				<td class="alignRight">Employer's Contribution: </td>
				<td >
				<s:textfield name="erpsContribution" cssStyle="width:50px !important;text-align:right;"  onkeypress="return isNumberKey(event)"></s:textfield> %
				<s:checkbox name="erpsContributionchbox" cssStyle="text-align:right;" ></s:checkbox> [Enable for employee deduction]
				</td>
			</tr>
			<tr>	
				<td class="alignRight">Max Limit: </td>
				<td ><s:textfield name="epsMaxLimit" cssStyle="width:50px !important;text-align:right;" onkeypress="return isNumberKey(event)" ></s:textfield>&nbsp;<%=uF.showData(currency, "") %></td>
			</tr>
			<tr>	
				<td  colspan="2"><strong>Employee Deposit Linked Insurance</strong></td>
			</tr>
			<tr>	
				<td class=" alignRight">Employer's Contribution: </td>
				<td >
				<s:textfield name="erdliContribution" cssStyle="width:50px !important;text-align:right;" onkeypress="return isNumberKey(event)" ></s:textfield> %
				<s:checkbox name="erdliContributionchbox" cssStyle="text-align:right;" ></s:checkbox> [Enable for employee deduction]
				</td>
			</tr>
			<tr>	
				<td class="alignRight">Max Limit: </td>
				<td ><s:textfield name="edliMaxLimit" cssStyle="width:50px !important;text-align:right;"  onkeypress="return isNumberKey(event)"></s:textfield>&nbsp;<%=uF.showData(currency, "") %></td>
			</tr>
			<tr>	
				<td  colspan="2"><strong>Admin Charges</strong></td>
			</tr>
			<tr>	
				<td class=" alignRight">EPF Admin Charges(Employer cont.): </td>
				<td >
				<s:textfield name="pfAdminCharges" cssStyle="width:50px !important;text-align:right;"  onkeypress="return isNumberKey(event)"></s:textfield> %
				<s:checkbox name="pfAdminChargeschbox" cssStyle="text-align:right;" ></s:checkbox> [Enable for employee deduction]
				</td>
			</tr>
			<tr>	
				<td class="alignRight">EDLI Admin Charges(Employer cont.): </td>
				<td >
				<s:textfield name="edliAdminCharges" cssStyle="width:50px !important;text-align:right;"  onkeypress="return isNumberKey(event)"></s:textfield> %
				<s:checkbox name="edliAdminChargeschbox" cssStyle="text-align:right;" ></s:checkbox> [Enable for employee deduction]
				</td>
			</tr>
			<tr>
				
				<% if(isLevels == 0) { %>
					<td colspan="2" align="center"><%=IMessages.ERRORM %> Please select level.<%=IMessages.END %></td>
				<% } else { %>
				<td></td>
				<td><input type="button" class="btn btn-primary" id="epfUpdate" name="epfUpdate" value="Update" onclick="getData(3)"/></td>
				<% } %>
			</tr>
		</table>

</s:form>


	<div class="pagetitle" style="margin:35px 0px 10px 0px">Previous Year EPF Calculation</div>
	<table  class="table table-bordered">
		<tr>
			<th class="alignCenter" valign="top" rowspan="2">FYI</th>
			<th class="alignCenter" colspan="4">EPF</th>
			<th class="alignCenter" colspan="2">EPS</th>
			<th class="alignCenter" colspan="2">EDLI</th>
			<th class="alignCenter" colspan="2">Admin</th>
			<th class="alignCenter" valign="top" rowspan="2">Salary Heads</th>
		</tr>
		
		<tr>
			<th class="alignCenter">Employee<br/>(%)</th>
			<th class="alignCenter">Max Limit<br/>(<%=uF.showData(currency, "") %>)</th>
			<th class="alignCenter">Employer<br/>(%)</th>
			<th class="alignCenter">Max Limit<br/>(<%=uF.showData(currency, "") %>)</th>
			<th class="alignCenter">Employer<br/>(%)</th>
			<th class="alignCenter">Max Limit<br/>(<%=uF.showData(currency, "") %>)</th>
			<th class="alignCenter">Employer<br/>(%)</th>
			<th class="alignCenter">Max Limit<br/>(<%=uF.showData(currency, "") %>)</th>
			<th class="alignCenter">EPF (Employer)<br/>(%)</th>
			<th class="alignCenter">EDLI (Employer)<br/>(%)</th>
		</tr>
	<%
		for(int i=0; i<alEPFSettings.size(); i++){ 
			List alInner = (List)alEPFSettings.get(i);
			if(alInner==null)alInner=new ArrayList();
	%>
			<tr>
				<td class="alignCenter"><%=uF.showData((String)alInner.get(0), "")%></td>
				<td class="alignRight"><%=uF.showData((String)alInner.get(1), "0")%></td>
				<td class="alignRight"><%=uF.showData((String)alInner.get(2), "0")%></td>
				<td class="alignRight"><%=uF.showData((String)alInner.get(3), "0")%></td>
				<td class="alignRight"><%=uF.showData((String)alInner.get(4), "0")%></td>
				<td class="alignRight"><%=uF.showData((String)alInner.get(5), "0")%></td>
				<td class="alignRight"><%=uF.showData((String)alInner.get(6), "0")%></td>
				<td class="alignRight"><%=uF.showData((String)alInner.get(7), "0")%></td>
				<td class="alignRight"><%=uF.showData((String)alInner.get(8), "0")%></td>
				<td class="alignRight"><%=uF.showData((String)alInner.get(9), "0")%></td>
				<td class="alignRight"><%=uF.showData((String)alInner.get(10), "0")%></td>
				<td class=""><%=uF.showData((String)alInner.get(11), "")%></td>
			</tr>
		<%}%>
	</table>
</div>