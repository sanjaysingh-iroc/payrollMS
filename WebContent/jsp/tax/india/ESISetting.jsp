<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String currency = (String)request.getAttribute("currency");
	
	LinkedHashMap hmESISettings = (LinkedHashMap)request.getAttribute("hmESISettings");
	if(hmESISettings==null)hmESISettings=new LinkedHashMap();

%>

<style>
.table_no_border>thead>tr>th, .table_no_border>tbody>tr>th, .table_no_border>tfoot>tr>th, 
.table_no_border>thead>tr>td, .table_no_border>tbody>tr>td, .table_no_border>tfoot>tr>td {
border-top: 1px solid #FFFFFF;
}

.ui-multiselect {
   
    width: 160px !important;
}
</style>
<script>
$(function() {
	$("input[name='esiUpdate']").click(function(){
		$(".validateRequired").prop('required',true);
	});
	$("#level").multiselect().multiselectfilter();
	$("#state").multiselect().multiselectfilter();
});	


function getData(type) {
	var strOrg='';
	var financialYear = document.getElementById("financialYear").value;
	var userscreen = document.getElementById("userscreen").value;
	var navigationId = document.getElementById("navigationId").value;
	var toPage = document.getElementById("toPage").value;
	//return confirm('Are you sure you want to update these settings?')
	
	if(type == 3) {
		var form_data = $("#idFrmESISetting").serialize();
		form_data = form_data + "&esiUpdate=Update";
		if(confirm('Are you sure you want to update these settings?')) { 
			$.ajax({
				url : 'ESISetting.action',
				data : form_data,
				cache:false,
				success:function(data){
					$("#actionResult").html(data);
				}
			});
		}
	} else {
		if(type == 1) {
			document.getElementById("level").selectedIndex = "-1";
		}
		var form_data = $("#idFrmESISetting").serialize();
		$.ajax({
			url : 'ESISetting.action',
			data : form_data,
			cache:false,
			success:function(data){
				$("#actionResult").html(data);
			}
		});
	}
}

function addElegibleHead(){
	var ddl1 = document.getElementById("strSalaryHeadId");
    var ddl2 = document.getElementById("salaryHeadEligible");
    
    for(i = ddl2.options.length - 1 ; i >= 0 ; i--) {
    	ddl2.remove(i);
    }
    
    for (var i=0; i < ddl1.options.length; i++) {
    	if (ddl1.options[i].selected == true) {
        	addOption(ddl2,ddl1.options[i].text,ddl1.options[i].value);
    	}
    }    
 }	
 
 function addOption(object,text,value) {
     var defaultSelected = false;
     var selected = false;
     var optionName = new Option(text, value, defaultSelected, selected)
     object.options[object.length] = optionName;
 }

</script>


<div class="box-body">

	<s:form name="frm_esiSetting" id="idFrmESISetting" theme="simple" action="ESISetting">
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
						<div style="float: left; width: 75%;">
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Financial Year</p>
								<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="financialYear" id="financialYear" onchange="getData(2);" cssClass="form-control autoWidth inline" />
							</div>
						</div>
					</div>
				</div>
			</div>
			
		<div class="col-md-12">
			<p style="font-size: 12px; font-style: italic;float:right">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>
		</div>
		
		<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
		<% session.setAttribute("MESSAGE", ""); %>
		<%-- <strong>Salary Heads to be considered</strong><br/>
		<div class="row row_without_margin">
			<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
				<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
					<s:select list="salaryHeadList" listValue="salaryHeadName" listKey="salaryHeadId" multiple="true" size="5" cssStyle="160px!important;"
					name="strSalaryHeadId" cssClass="salaryHeadName validateRequired" />
				</div>
				<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
					<s:select theme="simple" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" cssStyle="160px!important;" onchange="getData(0);" list="orgList"/>
				</div>
				<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
					<s:select list="stateList" name="state" listValue="stateName" listKey="stateId"  cssStyle="160px!important;" headerKey="" headerValue="Select State" 
					cssClass="validateRequired" onchange="getData(1);" />
				</div>
			</div>
		</div>
		<br/><br/>
		<strong>% share under Statutory Compliance</strong>
		<br/>
		<strong>Employee State Insurance Corporation</strong>	 --%>
		<table class="table table_no_border">
			<%-- <tr>	
				<td><strong>Salary Heads to be considered</strong></td>
				<td><strong>Salary Heads to be eligible</strong></td>
				<td><strong>Organisation</strong></td>
				<td><strong>Level</strong></td>
				<td><strong>State</strong></td>
			</tr> --%>
			<tr>	
				<td valign="top" style="padding-left:10px">
					<strong>Organisation</strong><br/>
					<s:select theme="simple" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="getData(1);" cssClass="validateRequired"
					 list="orgList" key="" cssStyle="width:200px !important"/>
				</td>
				
				<td valign="top" style="padding-left:10px">
					<strong>Level</strong><br/>
					<s:select theme="simple" name="level" id="level" cssClass="validateRequired" listKey="levelId" listValue="levelCodeName" list="levelList"
						key="" onchange="getData(2);" cssStyle="width:200px !important" multiple="true"/> <!-- headerKey="" headerValue="Select Level"  -->
				</td>
				
				<td valign="top" style="padding-left:10px">
					<strong>State</strong><br/>
					<s:select list="stateList" listValue="stateName" listKey="stateId" cssClass="validateRequired" name="state" id="state" 
						onchange="getData(2);" cssStyle="width:200px !important" multiple="true"></s:select> <!-- headerKey="" headerValue="Select State" -->
				</td>
			</tr>
			<tr>
				<td>
					<strong>Salary Heads to be considered</strong><br/>
					<s:select name="strSalaryHeadId" id="strSalaryHeadId" list="salaryHeadList" listValue="salaryHeadName" listKey="salaryHeadId"
						multiple="true" size="5" onclick="addElegibleHead();" cssClass="validateRequired" cssStyle="height:51px !important;"></s:select>		
				<%-- <s:select list="salaryHeadList" listValue="salaryHeadName" listKey="salaryHeadId" headerKey="" headerValue="Select State" 
						cssClass="validateRequired" name="state" onclick="addElegibleHead();" cssStyle="width:200px !important"></s:select>  --%>
				</td>
				<td>
					<strong>Salary Heads to be eligible</strong><br/>
					<s:select name="salaryHeadEligible" id="salaryHeadEligible" cssClass="validateRequired" list="salaryHeadEligibleList" 
						listValue="salaryHeadName" listKey="salaryHeadId" multiple="true" size="5" cssStyle="height:51px !important;"></s:select>		
				</td>
			</tr>
			<tr>
				<td colspan="3"><strong>% share under Statutory Compliance</strong></td>
			</tr>
			
			<tr>	
				<td colspan="3"><strong>Employee State Insurance Corporation</strong></td>
			</tr>
			<tr>	
				<td class="alignRight">Employee's Contribution: </td>
				<td colspan="2"><s:textfield name="eesiContribution" cssStyle="width:50px !important;text-align:right;" onkeypress="return isNumberKey(event)"></s:textfield> %<span class="hint">Employee's Contribution<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>	
				<td class="alignRight">Employer's Contribution: </td>
				<td colspan="2"><s:textfield name="ersiContribution" cssStyle="width:50px !important;text-align:right;" onkeypress="return isNumberKey(event)"></s:textfield> %</td>
			</tr>
			
			<tr>
				<td class="alignRight">Max Limit: </td>
				<td colspan="2"><s:textfield name="esiMaxLimit" cssStyle="width:50px !important;text-align:right;" onkeypress="return isNumberKey(event)"></s:textfield>&nbsp;<%=uF.showData(currency, "") %></td>
			</tr>
			<tr>	
				<td>&nbsp;</td>
				<td align="left" colspan="2"><input type="submit" class="btn btn-primary" name="esiUpdate" value="Update"/></td>  <!-- onclick="getData(3)" -->
			</tr>
		</table>
	
	</s:form>



	<div class="pagetitle" style="margin:10px 0px 10px 0px;clear: both;padding-top: 20px;">Previous Year ESI Calculation</div>
	<table width="100%" class="table table-bordered">
	
		<tr>
			<th valign="top" rowspan="2">Financial Year</th>
			<th valign="top" rowspan="2">Level</th>
			<th class="alignCenter" colspan="3">ESI</th>
			<th class="alignCenter" valign="top" rowspan="2">Salary Heads to be considered</th>
			<th class="alignCenter" valign="top" rowspan="2">Salary Heads to be eligible</th>
		</tr>
		
		<tr>
			<th class="alignCenter">Employee<br/>(%)</th>
			<th class="alignCenter">Employer<br/>(%)</th>
			<th class="alignCenter">Max Limit<br/>(<%=uF.showData(currency, "") %>)</th>
		</tr>

		<%
		Set set = hmESISettings.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			String strStateId = (String)it.next();
			List alEPFSettings = (List)hmESISettings.get(strStateId);
			if(alEPFSettings==null)alEPFSettings=new ArrayList();
		%>
		<tr>
			<th colspan="5" class="alignLeft"><%= strStateId%></th>
		</tr>
	
		<%
		for(int i=0; i<alEPFSettings.size(); i++){
			List alInner = (List)alEPFSettings.get(i);
			if(alInner==null)alInner=new ArrayList();
		%>
			<tr>
				<td class="alignCenter"><%=uF.showData((String)alInner.get(0), "")%></td>
				<td class="alignCenter"><%=uF.showData((String)alInner.get(6), "")%></td>
				<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(1), "0")%></td>
				<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(2), "0")%></td>
				<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(3), "0")%></td>
				<td><%=uF.showData((String)alInner.get(4), "-")%></td>
				<td><%=uF.showData((String)alInner.get(5), "-")%></td>
			</tr>
			
		<% } } %>
	</table>

</div>


<script type="text/javascript">

$("#idFrmESISetting").submit(function(e){
	e.preventDefault();
	var form_data = $("#idFrmESISetting").serialize();
	form_data = form_data + "&esiUpdate=Update";
	if(confirm('Are you sure you want to update these settings?')) {
		$.ajax({
			url : 'ESISetting.action',
			data : form_data,
			cache:false,
			success:function(data){
				$("#actionResult").html(data);
			}
		});
	}
});

</script>