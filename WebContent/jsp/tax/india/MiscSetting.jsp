<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
 --%>
<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
//List alEPFSettings = (List)request.getAttribute("alESISettings");
//if(alEPFSettings==null)alEPFSettings=new ArrayList();
LinkedHashMap hmMiscSettings = (LinkedHashMap)request.getAttribute("hmMiscSettings");
if(hmMiscSettings==null)hmMiscSettings=new LinkedHashMap();

%>

<script>

$(function() {
	$("input[name='miscUpdate']").click(function(){
		$(".validateNumber").prop('type','number');$(".validateNumber").prop('step','any');
	});
});	

function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function submitForm(type) {
	var form_data = $("#idFrmMiscSetting").serialize();
	if(type == 1) {
		if(confirm('Are you sure you want to update these settings?')) {
			$.ajax({
				url:'MiscSetting.action',
				data :form_data+"&miscUpdate=Update",
				cache:false,
				success:function(data){
					$("#actionResult").html(data);
				}
			});
		}
	} else {
		$.ajax({
			url:'MiscSetting.action',
			data :form_data,
			cache:false,
			success:function(data){
				$("#actionResult").html(data);
			}
		});
	}
	
}
</script>


<div class="box-body">

	<s:form name="frm_miscSetting" id="idFrmMiscSetting" theme="simple" action="MiscSetting">
		<s:hidden name="userscreen" id="userscreen" />
		<s:hidden name="navigationId" id="navigationId" />
		<s:hidden name="toPage" id="toPage" />
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
							<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="financialYear" onchange="submitForm(0);" />
						</div>
						<div style="float: left; margin-left: 10px;">
							<p style="padding-left: 5px;">State</p>
							<s:select list="stateList" listValue="stateName" listKey="stateId" headerKey="" headerValue="Select State" cssClass="validateRequired" name="state" onchange="submitForm(0);" />
						</div>
					</div>
				</div>
			</div>
		</div>
		
	
	
	
		<%-- <div style="margin:0px 0px 10px 0px;float:left"><span class="pagetitle">Other tax administration for FY</span>&nbsp&nbsp&nbsp <s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="financialYear" onchange="document.frm_miscSetting.submit();"></s:select></div> --%>
		<div class="col-md-12">
			<p style="font-size: 12px; padding-left: 42px;padding-right: 10px; font-style: italic;float:right">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>
		</div>
		
		<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
		<% session.setAttribute("MESSAGE", ""); %>
		
		<h4>Miscellaneous Tax Calculations</h4><hr style="border:solid 1px #000"/>
		<table class="table table_no_border" style="width: 55%">
			<tr>	
				<td class=" alignRight">Flat TDS: </td>
				<td><s:textfield name="flatTds" cssStyle="width:81px !important;text-align:right" cssClass = "validateNumber" onkeypress="return isNumberKey(event)"></s:textfield> %</td>
			</tr>
			
			<%-- <tr>	
				<td class=" alignRight">Service Tax: </td>
				<td><s:textfield name="serviceTax" cssStyle="width:81px !important;text-align:right" cssClass = "validateNumber" onkeypress="return isNumberKey(event)"></s:textfield> %</td>
			</tr>
			
			<tr>	
				<td class=" alignRight">Swachha Bharat Cess: </td>
				<td><s:textfield name="swachhaBharatCess" cssStyle="width:81px !important;text-align:right" cssClass = "validateNumber" onkeypress="return isNumberKey(event)"></s:textfield> %</td>
			</tr>
			
			
			<tr>	
				<td class=" alignRight">Krishi Kalyan Cess: </td>
				<td><s:textfield name="krishiKalyanCess" cssStyle="width:81px !important;text-align:right" cssClass = "validateNumber" onkeypress="return isNumberKey(event)"></s:textfield> %</td>
			</tr> --%>
			
			<%-- <tr>	
				<td class="label alignRight">CGST: </td>
				<td class="label"><s:textfield name="strCGST" cssStyle="width:72px;text-align:right" cssClass = "validateRequired" onkeypress="return isNumberKey(event)"></s:textfield> %</td>
			</tr> 
			<tr>	
				<td class="label alignRight">SGST: </td>
				<td class="label"><s:textfield name="strSGST" cssStyle="width:72px;text-align:right" cssClass = "validateRequired" onkeypress="return isNumberKey(event)"></s:textfield> %</td>
			</tr>	
			
			--%>
			
			<tr>	
				<td class="alignRight">CGST: </td>
				<td><s:textfield name="strCGST" cssStyle="width:81px !important; text-align:right" cssClass="validateRequired" onkeypress="return isNumberKey(event)"></s:textfield> %</td>
			</tr>
			
			<tr>	
				<td class="alignRight">SGST: </td>
				<td><s:textfield name="strSGST" cssStyle="width:81px !important; text-align:right" cssClass="validateRequired" onkeypress="return isNumberKey(event)"></s:textfield> %</td>
			</tr>			
			
			<tr>	
				<td class=" alignRight">Standard Cess: </td>
				<td><s:textfield name="standardCess" cssStyle="width:81px !important;text-align:right" cssClass="validateNumber" onkeypress="return isNumberKey(event)"></s:textfield> %</td>
			</tr>
			
			<tr>	
				<td class=" alignRight">Education Cess: </td>
				<td><s:textfield name="educationCess" cssStyle="width:81px !important;text-align:right" cssClass="validateNumber" onkeypress="return isNumberKey(event)"></s:textfield> %</td>
			</tr>
		</table>
		<h4>Tax Rebate Under Section 87 A (Less)</h4><hr style="border:solid 1px #000"/>
		<table class="table table_no_border" style="width: 47%">
			<tr>	
				<td class="alignRight">Maximum Net Taxable Income: </td>
				<td><s:textfield name="maxNetTaxIncome" cssStyle="width:81px !important;text-align:right" cssClass="validateNumber" onkeypress="return isNumberKey(event)"></s:textfield></td>
			</tr>
			<tr>	
				<td class="alignRight">Rebate Amount: </td>
				<td><s:textfield name="rebateAmt" cssStyle="width:81px !important;text-align:right" cssClass="validateNumber" onkeypress="return isNumberKey(event)"></s:textfield></td>
			</tr>
			
			
			<tr>	
				<td colspan="2" align="center"><input type="button" class="btn btn-primary" name="miscUpdate" value="Update" onclick="submitForm(1);"/></td>
			</tr>
		</table>
	</s:form>


	<div class="pagetitle" style="margin:35px 0px 10px 0px">Miscellaneous Tax Calculations</div>
		<table class="table table-bordered">
		
			<tr>
				<th>FYI</th>
				<th>Flat TDS<br/>(%)</th>
				<th>Service Tax<br/>(%)</th>
				<th>Swachha Bharat Cess<br/>(%)</th>
				<th>Krishi Kalyan Cess<br/>(%)</th>
				<th>CGST<br/>(%)</th>
				<th>SGST<br/>(%)</th>
				<th>Standard Cess<br/>(%)</th>
				<th>Education Cess<br/>(%)</th>
			</tr>
					
			<%
			Set set = hmMiscSettings.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()){
				String strStateId = (String)it.next();
				
				List alEPFSettings = (List)hmMiscSettings.get(strStateId);
				if(alEPFSettings==null)alEPFSettings=new ArrayList();
			%>
				<tr>
					<th colspan="6" class="alignLeft"><%= strStateId%></th>
				</tr>
				
				<%
				for(int i=0; i<alEPFSettings.size(); i++){
					List alInner = (List)alEPFSettings.get(i);
					if(alInner==null)alInner=new ArrayList();
				%>
					<tr>
						<td class="alignCenter"><%=uF.showData((String)alInner.get(0), "")%></td>
							<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(1), "0")%></td>
							<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(2), "0")%></td>
							<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(7), "0")%></td>
							<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(8), "0")%></td>
							<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(9), "0")%></td>
							<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(10), "0")%></td>
							<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(3), "0")%></td>
							<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(4), "0")%></td>
					</tr>
			<% } } %>
		</table>


	<div class="pagetitle" style="margin:35px 0px 10px 0px">Tax Rebate Under Section 87 A (Less)</div>
		<table class="table table-bordered">
			<tr>
				<th class="alignCenter">FYI</th>
				<th class="alignCenter">Maximum Taxable Income</th>
				<th class="alignCenter">Rebate Amount</th>
			</tr>
					
			<%
			Set set1 = hmMiscSettings.keySet();
			Iterator it1 = set.iterator();
			while(it1.hasNext()) {
				String strStateId = (String)it1.next();
				List alEPFSettings = (List)hmMiscSettings.get(strStateId);
				if(alEPFSettings==null) alEPFSettings = new ArrayList();
			%>
				<tr>
					<th colspan="3" class="alignLeft"><%= strStateId%></th>
				</tr>
				<%
				for(int i=0; i<alEPFSettings.size(); i++){
					List alInner = (List)alEPFSettings.get(i);
					if(alInner==null)alInner=new ArrayList();
				%>
					<tr>
						<td class="alignCenter"><%=uF.showData((String)alInner.get(0), "")%></td>
						<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(5), "0")%></td>
						<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(6), "0")%></td>
					</tr>
			<% } } %>
		</table>
	</div>
	
	