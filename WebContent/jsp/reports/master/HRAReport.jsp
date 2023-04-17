<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<style>
.table_no_border>thead>tr>th, .table_no_border>tbody>tr>th, .table_no_border>tfoot>tr>th, 
.table_no_border>thead>tr>td, .table_no_border>tbody>tr>td, .table_no_border>tfoot>tr>td {
border-top: 1px solid #FFFFFF;
}
</style>
<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
List alHRASettings = (List)request.getAttribute("alHRASettings");
if(alHRASettings==null) alHRASettings=new ArrayList();
Map hmHRASettings = (Map)request.getAttribute("hmHRASettings");
if(hmHRASettings==null) hmHRASettings=new HashMap();

%>
 
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>
$(function(){
	$(".salaryHeadName").multiselect().multiselectfilter();
	$("input[name='hraUpdate']").click(function(){ 
		$(".validateRequired").prop('required',true); 
	});
});

function getData(type) {
	var financialYear = document.getElementById("financialYear").value;
	var userscreen = document.getElementById("userscreen").value;
	var navigationId = document.getElementById("navigationId").value;
	var toPage = document.getElementById("toPage").value;
	
	window.location='MyDashboard.action?strCFYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
}
	
</script> 
 

	<div class="leftbox reportWidth">

	<s:form name="frm_HRASetting" theme="simple" action="MyDashboard">
	
		<s:hidden name="userscreen" id="userscreen" />
		<s:hidden name="navigationId" id="navigationId" />
		<s:hidden name="toPage" id="toPage" />
		<input type="hidden" name="strCFYear" id="strCFYear" value="<%=(String)request.getAttribute("financialYear") %>" />
		<s:hidden name="strMiscId" />
		
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
					<div style="float: left; width: 75%;">
						<div style="float: left; margin-left: 10px;">
							<p style="padding-left: 5px;">Financial Year</p>
							<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="financialYear" id="financialYear" onchange="getData('0');"/>
						</div>
					</div>
				</div>
			</div>
		</div>
		
	<div class="col-md-12">
		<p style="font-size:12px; padding-left:42px; padding-right:10px; font-style:italic; float:right;">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>
	</div>
	
	<%=uF.showData((String) session.getAttribute("MESSAGE"), "") %>
	<% session.setAttribute("MESSAGE", ""); %>

		
	
	<%-- <div style="margin:0px 0px 10px 0px;float:left"> 
		<span class="pagetitle">HRA Administration for FY </span>
		<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="financialYear" onchange="document.frm_HRASetting.submit();"/>
	</div> --%>

		<table style="clear: both;" class="table table_no_border">
			<tr>
				<td colspan="2"><strong>Salary Heads to be considered</strong></td>
			</tr>
			<tr>	
				<td colspan="2">
					<s:select list="salaryHeadList" listValue="salaryHeadName" listKey="salaryHeadId" multiple="true" size="5" name="strSalaryHeadId" cssClass="salaryHeadName validateRequired" />
				</td>
			</tr>
			
			<tr>	
				<td  colspan="2"><strong>% share under Statutory Compliance</strong></td>
			</tr>
			
			<tr>	
				<td class="alignRight">Rent Paid - x% of salary: </td>
				<td ><s:textfield name="strCond1" cssStyle="text-align:right; width: 55px !important;" onkeypress="return isNumberKey(event)"  /> %</td>
			</tr>
			
			<tr>	
				<td class="alignRight">x% of salary in metro cities: </td>
				<td ><s:textfield name="strCond2" cssStyle="text-align:right; width: 55px !important;"  onkeypress="return isNumberKey(event)" /> %</td>
			</tr>
			
			<tr>
				<td class="alignRight">x% of salary in other cities: </td>
				<td ><s:textfield name="strCond3" cssStyle="text-align:right; width: 55px !important;" onkeypress="return isNumberKey(event)" /> %</td>
			</tr>
			
			<tr>	
				<td colspan="2" align="center"><input type="submit" class="btn btn-primary" name="hraUpdate" value="Update"/></td>
			</tr>
		</table>
	</s:form>

	<div class="pagetitle" style="margin:10px 0px 10px 0px">Previous Year HRA Calculation</div>
	<table width="100%" class="table table-bordered">
		<tr>
			<th class="alignCenter">FYI</th>
			<th class="alignCenter">Rent Paid - x % of salary</th>
			<th class="alignCenter">x % of salary in metro cities</th>
			<th class="alignCenter">x % of salary in other cities</th>
			<th class="alignCenter">Salary Heads</th>
		</tr>

		<%
		for(int i=0; i<alHRASettings.size(); i++) {
			List alInner = (List)alHRASettings.get(i);
			if(alInner==null)alInner=new ArrayList();
		%>
			<tr>
				<td class="alignCenter"><%=uF.showData((String)alInner.get(0), "")%></td>
				<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(1), "0")%></td>
				<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(2), "0")%></td>
				<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(3), "0")%></td>
				<td><%=uF.showData((String)alInner.get(4), "")%></td>
			</tr>
		<% } %>
	</table>
</div>

