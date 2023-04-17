<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div id="divResult">
	<script type="text/javascript" charset="utf-8">
	
		$(document).ready(function() {
			
			$("#strStartDate").datepicker({
		        format: 'dd/mm/yyyy',
		        autoclose: true
		    }).on('changeDate', function (selected) {
		        var minDate = new Date(selected.date.valueOf());
		        $('#strEndDate').datepicker('setStartDate', minDate);
		    });
		    
		    $("#strEndDate").datepicker({
		    	format: 'dd/mm/yyyy',
		    	autoclose: true
		    }).on('changeDate', function (selected) {
		            var minDate = new Date(selected.date.valueOf());
		            $('#strStartDate').datepicker('setEndDate', minDate);
		    });
			
			$("#f_strWLocation").multiselect().multiselectfilter();
			$("#f_department").multiselect().multiselectfilter();
			$("#f_service").multiselect().multiselectfilter();
			$("#f_level").multiselect().multiselectfilter();
		});
	
		function submitForm(type) {
			
			document.frm_PartnerwiseMISReport.exportType.value='';
			var org = document.getElementById("f_org").value;
			/* var strMonth = document.getElementById("strMonth").value;
			var financialYear = document.getElementById("financialYear").value; */
			var strStartDate = document.getElementById("strStartDate").value;
			var strEndDate = document.getElementById("strEndDate").value;
			var location = getSelectedValue("f_strWLocation");
			var department = getSelectedValue("f_department");
			var service = getSelectedValue("f_service");
			var level = getSelectedValue("f_level");
			var paramValues = "";
			if(type == '2') {
				/* paramValues = '&strLocation='+location+'&strMonth='+strMonth+'&financialYear='+financialYear+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level; */
				paramValues = '&strLocation='+location+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level;
			}
			//alert("paramValues ===>> " + paramValues);
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'PartnerwiseMISReport.action?f_org='+org+paramValues,
				data: $("#"+this.id).serialize(),
				success: function(result){
		        	//console.log(result);
		        	$("#divResult").html(result);
		   		}
			});
		}
		
		function getSelectedValue(selectId) {
			var choice = document.getElementById(selectId);
			var exportchoice = "";
			for ( var i = 0, j = 0; i < choice.options.length; i++) {
				if (choice.options[i].selected == true) {
					if (j == 0) {
						exportchoice = choice.options[i].value;
						j++;
					} else {
						exportchoice += "," + choice.options[i].value;
						j++;
					}
				}
			}
			return exportchoice;
		}
		
		function generateReportExcel(){
			document.frm_PartnerwiseMISReport.exportType.value='excel';
			document.frm_PartnerwiseMISReport.submit();
		}
	
	    
	</script>
	
	<%
		/* List alPartnerList = (List) request.getAttribute("alPartnerList"); */
		Map<String, String> hmPartner = (Map<String, String>) request.getAttribute("hmPartner");
		if(hmPartner == null) hmPartner = new HashMap<String, String>();
		
		Map<String, String> hmReportData = (Map<String, String>) request.getAttribute("hmReportData");
		if(hmReportData == null) hmReportData = new HashMap<String, String>();
	%>
	
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="desgn" style="margin-bottom: 5px; color:#232323;">
			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
				<div class="box-header with-border">
					<h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					<s:form name="frm_PartnerwiseMISReport" id="frm_PartnerwiseMISReport" action="PartnerwiseMISReport" theme="simple" method="post">
						<s:hidden name="exportType"></s:hidden>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
								</div>
								
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Service</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
								</div>
								
							</div>
						</div><br>
						 <div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<%-- <div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div id="financialYearDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerValue="Select Financial Year" list="financialYearList" />
					      		</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select theme="simple" name="strMonth" id="strMonth" headerKey="0" headerValue="Select Month" listKey="monthId" listValue="monthName" list="monthList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div> --%>
							
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">From Date</p>
								<s:textfield name="strStartDate" id="strStartDate" cssStyle="width:100px !important;" readonly="true"></s:textfield>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-right: 5px;">To Date</p>
					    		<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;" readonly="true"></s:textfield>
							</div>
							
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div>
						</div>
					</s:form>
				</div>
	          <!-- /.box-body -->
			</div>
		</div>
		
		<div class="box-body">
			<div class="col-md-2 pull-right">
					<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
			</div>
		</div>
		<div class="box-body" style="padding: 5px; overflow-y: auto;">
			<table cellspacing="1" class="table table-bordered" id=lt1>
				<tr>
					<th></th>
					<th class="alignCenter" colspan="<%=hmPartner.size()+1%>">Partner wise MIS till date</th>
				</tr>
				<tr>
					<th></th>
					<th class="alignCenter">Total KPCA</th>
					<%-- <% for(int i=0; i<alPartnerList.size(); i++){ %>
					<th ><%= alPartnerList.get(i) %></th>
					<% } %> --%>
					<%
						List<String> alPartnerId = new ArrayList<String>();
						Iterator<String> it = hmPartner.keySet().iterator();
						StringBuilder sbOPE = new StringBuilder();
						StringBuilder sbPE = new StringBuilder();
						StringBuilder sbEmpCost = new StringBuilder();
						StringBuilder sbTC = new StringBuilder();
						StringBuilder sbTNC = new StringBuilder();
						StringBuilder sbTotRec = new StringBuilder();
						StringBuilder sbTotDE = new StringBuilder();
						
						while (it.hasNext()) {
							String partnerId = it.next();
							alPartnerId.add(partnerId);
							sbOPE.append("<td class=\"alignRight\">"+hmReportData.get(partnerId+"_OTHER_DIRECT_COST")+"</td>");
							sbPE.append("<td class=\"alignRight\">"+hmReportData.get(partnerId+"_PROFESSIONAL_COST")+"</td>");
							sbEmpCost.append("<td class=\"alignRight\">"+hmReportData.get(partnerId+"_EMP_COST")+"</td>");
							sbTC.append("<td class=\"alignRight\">"+hmReportData.get(partnerId+"_CHARGEBLE")+"</td>");
							sbTNC.append("<td class=\"alignRight\">"+hmReportData.get(partnerId+"_NON_CHARGEBLE")+"</td>");
							
							sbTotRec.append("<td class=\"alignRight\">"+hmReportData.get(partnerId+"_TOTAL_RECEIPT")+"</td>");
							sbTotDE.append("<td class=\"alignRight\">"+hmReportData.get(partnerId+"_TOTAL_DIRECT_EXP")+"</td>");
					%>
					
						<th class="alignCenter"><%=hmPartner.get(partnerId) %></th>
						
					<% } %>
				</tr>
				<tr>
					<td class="alignLeft">Annual Committed</td>
					<td></td>
				</tr>
				<tr>
					<td class="alignLeft"><u>Receipts</u></td>
					<td></td>
				</tr>
				<tr>
					<td class="alignLeft">a) Professional Fees</td>
					<td><%=hmReportData.get("_TOTAL_PROFESSIONAL_COST") %></td>
						<%=sbPE.toString() %>
					<%-- <% for(int i=0; i<alPartnerId.size(); i++){ %>
						<td class="alignLeft"><%=hmReportData.get(alPartnerId.get(i)+"_PROFESSIONAL_COST") %></td>
					<% } %> --%>
					
				</tr>
				<tr>
					<td class="alignLeft">b) Inter Firm Billing</td>
					<td></td>
				</tr>
				<tr>
					<td class="alignLeft">b) Out Of Pocket</td>
					<td><%=hmReportData.get("_TOTAL_OTHER_DIRECT_COST") %></td>
					<%-- <% for(int i=0; i<alPartnerId.size(); i++){ %>
						<td class="alignLeft"><%=hmReportData.get(alPartnerId.get(i)+"_OTHER_DIRECT_COST") %></td>
					<% } %> --%>
					<%=sbOPE.toString() %>
				</tr>
				<tr>
					<th class="alignLeft">Total Receipts</th>
					<td><%=hmReportData.get("_TOTAL_RECEIPT_AMT") %></td>
					<%=sbTotRec.toString() %>
				</tr>
				<tr>
					<td class="alignLeft"><u>Direct Expenses</u></td>
					<td></td>
				</tr>
				<tr>
					<td class="alignLeft">a) Employees Cost</td>
					<td><%=hmReportData.get("_TOTAL_EMP_COST") %></td>
					<%-- <% for(int i=0; i<alPartnerId.size(); i++){ %>
						<td class="alignLeft"><%=hmReportData.get(alPartnerId.get(i)+"_EMP_COST") %></td>
					<% } %> --%>
					<%=sbEmpCost.toString() %>
				</tr>
				<tr>
					<td class="alignLeft">b) Professional Fees</td>
					<%-- <td><%=hmReportData.get("_TOTAL_PROFESSIONAL_COST") %></td> --%>
					<%-- <% for(int i=0; i<alPartnerId.size(); i++){ %>
						<td class="alignLeft"><%=hmReportData.get(alPartnerId.get(i)+"_PROFESSIONAL_COST") %></td>
					<% } %> --%>
					<%-- <%=sbPE.toString() %> --%>
				</tr>
				<tr>
					<td class="alignLeft">c) Travelling C.</td>
					<td><%=hmReportData.get("_TOTAL_CHARGEBLE") %></td>
					<%-- <% for(int i=0; i<alPartnerId.size(); i++){ %>
						<td class="alignLeft"><%=hmReportData.get(alPartnerId.get(i)+"_CHARGEBLE") %></td>
					<% } %> --%>
					<%=sbTC.toString() %>
				</tr>
				<tr>
					<td class="alignLeft">d) Travelling N.C.</td>
					<td><%=hmReportData.get("_TOTAL_NON_CHARGEBLE") %></td>
					<%-- <% for(int i=0; i<alPartnerId.size(); i++){ %>
						<td class="alignLeft"><%=hmReportData.get(alPartnerId.get(i)+"_NON_CHARGEBLE") %></td>
					<% } %> --%>
					<%=sbTNC.toString() %>
				</tr>
				<tr>
					<td class="alignLeft">e) Other Direct cost</td>
					<%-- <td><%=hmReportData.get("_TOTAL_OTHER_DIRECT_COST") %></td> --%>
					<%-- <% for(int i=0; i<alPartnerId.size(); i++){ %>
						<td class="alignLeft"><%=hmReportData.get(alPartnerId.get(i)+"_OTHER_DIRECT_COST") %></td>
					<% } %> --%>
					<%-- <%=sbOPE.toString() %> --%>
				</tr>
				<tr>
					<td class="alignLeft">f) Inter Firm Billing</td>
					<td class="alignLeft"></td>
				</tr>
				<tr>
					<th class="alignLeft">Total Direct Expenses</th>
					<td><%=hmReportData.get("_TOTAL_DIRECT_EXP") %></td>
					<%=sbTotDE.toString() %>
				</tr>
				<tr>
					<th class="alignLeft">Contribution </th>
					<td></td>
				</tr>
				<tr>
					<td class="alignLeft">Partner Cost</td>
					<td></td>
				</tr>
				<tr>
					<th class="alignLeft">Net Contribution </th>
					<td></td>
				</tr>
				<tr>
					<td class="alignLeft"><u>Indirect Expenses</u></td>
					<td></td>
				</tr>
				<tr>
					<th class="alignLeft">NET SURPLUS</th>
					<td></td>
				</tr>
			</table>
		</div>
	</div>

</div>