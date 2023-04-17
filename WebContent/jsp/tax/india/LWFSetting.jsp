<%@page import="com.konnect.jpms.util.IMessages"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	LinkedHashMap hmESISettings = (LinkedHashMap)request.getAttribute("hmESISettings");
	if(hmESISettings==null)hmESISettings=new LinkedHashMap();
	List<List<String>> alOuter = (List)request.getAttribute("alOuter");
%>


<script> 


	var cnt=<%=alOuter.size()%>;
	
	function addLWF() {
		
		++cnt;
		var divTag = document.createElement("div");
	    divTag.id = "row_lwf_"+cnt;
	    divTag.setAttribute("class", "");
		divTag.innerHTML = 	"<table class=\"table table_no_border\" style=\"border-top:1px solid #ccc\">"+

			"<tr>"+	
				"<td class=\" alignRight\">Employee\'s Contribution: </td>"+
				"<td class=\"\"><input type=\"text\" name=\"eelwfContribution\" style=\"width: 51px !important;text-align:right;\" class=\"validateNumber\" onkeypress=\"return isNumberKey(event)\"/>&nbsp;&nbsp;<span class=\"rupee\"><i class=\"fa fa-inr\"></i></span></td>"+
			"</tr>"+
			
			"<tr>	"+
				"<td class=\" alignRight\">Employer\'s Contribution: </td>"+
				"<td class=\"\"><input type=\"text\" name=\"erlwfContribution\" style=\"width: 51px !important;text-align:right;\" class=\"validateNumber\" onkeypress=\"return isNumberKey(event)\"/>&nbsp;&nbsp;<span class=\"rupee\"><i class=\"fa fa-inr\"\></i></span></td>"+
			"</tr>"+
			
			"<tr>	"+
				"<td colspan=\"2\" class=\" alignRight\" style=\"text-align: center;padding-left:10px\">Min Limit: <input type=\"text\" name=\"elwfMinLimit\" style=\"width: 51px !important;text-align:right\" class=\"validateNumber\" onkeypress=\"return isNumberKey(event)\"/>&nbsp;&nbsp;<span class=\"rupee\"><i class=\"fa fa-inr\"></i></span>"+
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
				"Max Limit: <input type=\"text\" name=\"elwfMaxLimit\" style=\"width: 51px !important;text-align:right\" class=\"validateNumber\" onkeypress=\"return isNumberKey(event)\"/>&nbsp;<span class=\"rupee\"><i class=\"fa fa-inr\"></i></span></td>"+
			"</tr>"+

			"<tr>"+
	    	"<td colspan=\"2\"><a href=\"javascript:void(0)\" onclick=\"removeLWF(this.id)\" id=\""+cnt+"\" class=\"remove-font pull-right\"></a></td>"+
	    	"</tr>"+
	    	"</table>"; 
	    document.getElementById("div_lwf").appendChild(divTag);
	    
	}

	function removeLWF(removeId) {
		
		var remove_elem = "row_lwf_"+removeId;
		var row_skill = document.getElementById(remove_elem); 
		document.getElementById("div_lwf").removeChild(row_skill);
		
	}
	
function removeLWFA(removeId) {
	if(confirm('Are you sure you want to delete this slab?')){
		var remove_elem = "row_lwf_"+removeId;
		var row_skill = document.getElementById(remove_elem);
		document.getElementById("div_lwf_1").removeChild(row_skill);	
	}
}
	
function isNumberKey(evt)
{
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
      return false;
   }
   return true;
}	
	
$(function() {
	$("input[name='lwfUpdate']").click(function(){
		$(".validateNumber").prop('type','number');
		$(".validateNumber").prop('step', 'any');
		$(".validateRequired").prop('required', true);
	});
	$("#idFrmLWFSetting_strSalaryHeadId").multiselect().multiselectfilter();
	$("#idFrmLWFSetting_strMonth").multiselect().multiselectfilter();
});


function submitForm(type){
	var form_data = $("#idFrmLWFSetting").serialize();
	$.ajax({
		url: 'LWFSetting.action',
		data : form_data,
		cache:false,
		success:function(data){
			$("#actionResult").html(data);
		}
	});
}


$("#idFrmLWFSetting").submit(function(event){
	event.preventDefault();
	var form_data = $("#idFrmLWFSetting").serialize();
	$.ajax({
		type : 'POST',
		url: 'LWFSetting.action',
		data : form_data+"&lwfUpdate=Update",
		success : function(data){
			$("#actionResult").html(data);
		}
	});
});

</script>


<div class="box-body">

	<%-- <s:form name="frm_lwfSetting1" id="idFrmLWFSetting1" theme="simple" action="LWFSetting" method="post">
	<div style="margin:0px 0px 10px 0px;float:left"><span class="pagetitle">LWF Administration for FY</span>&nbsp&nbsp&nbsp <s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="financialYear" onchange="document.frm_lwfSetting1.submit();" cssClass="form-control autoWidth inline"></s:select></div>
	<p style="font-size: 10px; padding-left: 42px;padding-right: 10px; font-style: italic;float:right">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>
	
	</s:form> --%>

	<s:form name="frm_lwfSetting" id="idFrmLWFSetting" theme="simple" action="LWFSetting" method="post">
		<s:hidden name="userscreen" id="userscreen" />
		<s:hidden name="navigationId" id="navigationId" />
		<s:hidden name="toPage" id="toPage" />
			
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
								<p style="padding-left: 5px;">Organisation</p>
								<s:select theme="simple" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="submitForm(1);" cssClass="validateRequired" list="orgList" key=""/>
							</div>
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Financial Year</p>
								<s:select list="financialYearList" listValue="financialYearName" listKey="financialYearId" name="financialYear" onchange="submitForm(1);" />
							</div>
							
						</div>
					</div>
				</div>
			</div>
			
			<div class="col-md-12">
				<p style="font-size:10px; padding-left:42px; padding-right:10px; font-style:italic; float:right;">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>
			</div>
			
		<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
		<% session.setAttribute("MESSAGE", ""); %>
	
			<table class="table table_no_border">
				<tr>	
					<td colspan="2"><strong>Salary Heads to be considered</strong></td>
				</tr>
				<tr>
					<td colspan="2">
						<div style="float: left;">
							<s:select list="salaryHeadList" listValue="salaryHeadName" listKey="salaryHeadId" multiple="true" size="5" name="strSalaryHeadId" id="idFrmLWFSetting_strSalaryHeadId" cssClass="validateRequired" cssStyle="width: 150px !important;"/>
						</div>
						<div style="float: left; margin-left: 10px;">
							<s:select list="stateList" listValue="stateName" listKey="stateId" headerKey="" headerValue="Select State" 
								cssClass="form-control autoWidth inline" name="state" cssClass="validateRequired" onchange="submitForm(1);" cssStyle="width: 150px !important;"/>
						</div>	
						<div style="float: left; margin-left: 10px;">
							<s:select list="monthList" listValue="monthName" listKey="monthId" multiple="true" size="5" name="strMonth" id="idFrmLWFSetting_strMonth" cssClass="validateRequired" cssStyle="width: 150px !important;"/>
						</div>
					</td>
				</tr>
			
				<%-- <tr>	
					<td class="" colspan="2"><strong>% share under Statutory Compliance</strong></td>
				</tr> --%>
				
				<tr>
					<td><strong>Labour welfare fund deductions</strong></td>
					<td align="right"><a style="float:right" href="javascript:void()" onclick="addLWF();"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add New Slab</a></td>
				</tr>
				
				<tr>
					<td colspan="2" id="div_lwf_1">
					<%
					int i=0;
					for(i=0; alOuter!=null && i<alOuter.size(); i++) {
						List alInner = (List)alOuter.get(i);
					%>
						<table class="table table_no_border" style="border-top:1px solid #ccc; margin-bottom: 0px;" id="row_lwf_<%=i%>" width="70%">
				
							<tr>	
								<td class=" alignRight">Employee's Contribution: </td>
								<td class=""><input type="text" value="<%=alInner.get(0)%>" name="eelwfContribution" style="width: 51px !important; text-align:right;" class="validateNumber" onkeypress="return isNumberKey(event)"/>&nbsp;&nbsp;<span class="rupee"><i class="fa fa-inr"></i></span></td>
							</tr>
							
							<tr>	
								<td class=" alignRight">Employer's Contribution: </td>
								<td class=""><input type="text" value="<%=alInner.get(1)%>" name="erlwfContribution" style="width: 51px !important;text-align:right;" class="validateNumber" onkeypress="return isNumberKey(event)"/>&nbsp;&nbsp;<span class="rupee"><i class="fa fa-inr"></i></span></td>
							</tr>
							
							<tr>	
								<td colspan="2" class=" alignRight" style="text-align: center;padding-left:10px">Min Limit: <input value="<%=alInner.get(2)%>" type="text" name="elwfMinLimit" style="width: 51px !important; text-align:right;" class="validateNumber" onkeypress="return isNumberKey(event)"/>&nbsp;&nbsp;<span class="rupee"><i class="fa fa-inr"></i></span>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								Max Limit: <input value="<%=alInner.get(3)%>" type="text" name="elwfMaxLimit" style="width: 51px !important; text-align:right;" class="validateNumber" onkeypress="return isNumberKey(event)"/>&nbsp;&nbsp;<span class="rupee"><i class="fa fa-inr"></i></span></td>
							</tr>
							
							<tr>
				    		   	<td colspan="2"><a href="javascript:void(0)" onclick="removeLWFA(this.id)" id="<%=i%>" class="remove-font pull-right"></a></td>
				    		</tr>
				    	</table>
					<% } %>
					</td>
				</tr>	
				  
				<tr>
					<td colspan="2">
						<% if(i==0) { %>
							<table class="table table_no_border" style="border-top:1px solid #ccc">
								<tr>	
									<td class="alignRight">Employee's Contribution: </td>
									<td><input type="text" name="eelwfContribution" style="width: 51px !important; text-align:right;" class="validateNumber" onkeypress="return isNumberKey(event)"/>&nbsp;&nbsp;<span class="rupee"><i class="fa fa-inr"></i></span></td>
								</tr>
								
								<tr>	
									<td class="alignRight">Employer's Contribution: </td>
									<td><input type="text" name="erlwfContribution" style="width: 51px ! important; text-align:right;" class="validateNumber" onkeypress="return isNumberKey(event)"/>&nbsp;&nbsp;<span class="rupee"><i class="fa fa-inr"></i></span></td>
								</tr>
								
								<tr>
									<td colspan="2" class="alignRight" style="text-align:center; padding-left:10px;">Min Limit: <input type="text" name="elwfMinLimit" style="width: 51px !important;text-align:right;" class="validateNumber" onkeypress="return isNumberKey(event)"/>&nbsp;&nbsp;<span class="rupee"><i class="fa fa-inr"></i></span>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									Max Limit: <input type="text" name="elwfMaxLimit" style="width: 51px !important;text-align:right;" class="validateNumber" onkeypress="return isNumberKey(event)"/>&nbsp;&nbsp;<span class="rupee"><i class="fa fa-inr"></i></span></td>
								</tr>
							</table>
						<% } %>
					</td>
				</tr>
					
				<tr>
					<td colspan="2">
						<div id="div_lwf"></div>
					</td>
				</tr>
				
				<tr>	
					<td colspan="2" align="center"><input type="submit" class="btn btn-primary" name="lwfUpdate" value="Update"/></td> <!-- onclick="submitForm(3)" -->
				</tr>
			</table>
		</s:form>


			<div style="margin:35px 0px 10px 0px"><span class="pagetitle">Previous Year LWF Calculation</span></div>
			<table class="table table-bordered">
			
				<tr>
					<th valign="top" rowspan="2" class="alignCenter">FYI</th>
					<th colspan="4" class="alignCenter">LWF</th>
					<th valign="top" rowspan="2" class="alignCenter">Salary Heads</th>
					<th valign="top" rowspan="2" class="alignCenter">Payable Months</th>
				</tr>
				
				<tr>
					<th class="alignCenter">Employee</th>
					<th class="alignCenter">Employer</th>
					<th class="alignCenter">Min Limit</th>
					<th class="alignCenter">Max Limit</th>
				</tr>

	<%
	
	Set set = hmESISettings.keySet();
	Iterator it = set.iterator();
	while(it.hasNext()) {
		String strStateId = (String)it.next();
		List alEPFSettings = (List)hmESISettings.get(strStateId);
		if(alEPFSettings==null)alEPFSettings=new ArrayList();
	%>
	<tr>
		<th colspan="7" class="alignLeft"><%=strStateId %></th>
	</tr>
	
	<%
	for(int i=0; i<alEPFSettings.size(); i++) {
		List alInner = (List)alEPFSettings.get(i);
		if(alInner==null)alInner=new ArrayList();
	%>
		<tr>
			<td class="alignCenter"><%=uF.showData((String)alInner.get(0), "")%></td>
			<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(1), "0")%></td>
			<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(2), "0")%></td>
			<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(3), "0")%></td>
			<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(4), "0")%></td>
			<td class=""><%=uF.showData((String)alInner.get(5), "0")%></td>
			<td class=""><%=uF.showData((String)alInner.get(6), "0")%></td>
		</tr>
			
	<% } } %>
</table>


</div>