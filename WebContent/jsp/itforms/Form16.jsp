<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>

<div id="divResult">

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	Map hmSalaryHeadMap = (Map) request.getAttribute("hmSalaryHeadMap"); 
	
	Map<String, String> hmEmployeeMap = (Map<String, String>) request.getAttribute("hmEmployeeMap");
	if (hmEmployeeMap == null) hmEmployeeMap = new LinkedHashMap<String, String>();
	
	Map<String, String> hmEmpSlabType = (Map<String, String>) request.getAttribute("hmEmpSlabType");
	if (hmEmpSlabType == null) hmEmpSlabType = new LinkedHashMap<String, String>();
	
	Map hmPayrollDetails = (Map) request.getAttribute("hmPayrollDetails");
	
	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
	if (strFinancialYearStart != null && strFinancialYearEnd != null) {
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart,IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd,IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}
	String pageCount = (String)request.getAttribute("pageCount");
	
	String sbData = (String) request.getAttribute("sbData");
	String strSearch = (String) request.getAttribute("strSearch");
	Map<String, Map<String, String>> hmEmpForm16Status = (Map<String, Map<String, String>>)request.getAttribute("hmEmpForm16Status");
	if(hmEmpForm16Status == null) hmEmpForm16Status = new HashMap<String, Map<String, String>>();
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");

%>

<style type="text/css">
.highslide-wrapper .highslide-html-content {
    width: 650px;
}
</style>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/highslide/highslide-with-html.js"> </script>
<link type="text/css" rel="stylesheet" href="<%= request.getContextPath()%>/css/highslide/highslide.css" />

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();

	$('#lt1').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	
});

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var strSearch = document.getElementById("strSearch").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
			+'&financialYear='+financialYear;
	}
	if(type == '3') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
			+'&financialYear='+financialYear+'&strSearch='+strSearch;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Form16.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
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

function generatePdf(empid) {
	/* var financialYear=document.frm_from16.financialYear.value;
	var url='Form16PdfReport.action?formType=form16&emp_id='+empid;
	url+="&financialYear="+financialYear;
	window.location = url;  */
	
	document.frm_from16.proPage.value = '';
	document.frm_from16.minLimit.value = '';
	document.frm_from16.emp_id.value = empid;
	document.frm_from16.formType.value = 'form16';
	document.frm_from16.submit();
}

function approveAndRelease(empid,empName){
	if(confirm('Are you sure, you want aprrove and release Form 16 of '+empName)){
		var org = document.getElementById("f_org").value;
		var financialYear = document.getElementById("financialYear").value;
		var strSearch = document.getElementById("strSearch").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var paramValues = "";
			paramValues = '&f_org'+org+'&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
				+'&financialYear='+financialYear+'&strSearch='+strSearch;
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'Form16.action?emp_id='+empid+'&formType=approveRelease'+paramValues,
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
	}
}

function revokeForm16(empid,empName){
	if(confirm('Are you sure, you want revoke Form 16 of '+empName)){
		var org = document.getElementById("f_org").value;
		var financialYear = document.getElementById("financialYear").value;
		var strSearch = document.getElementById("strSearch").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var paramValues = "";
			paramValues = '&f_org'+org+'&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
				+'&financialYear='+financialYear+'&strSearch='+strSearch;
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'Form16.action?emp_id='+empid+'&formType=revoke'+paramValues,
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
	}
}

/* function submitForm(){
	document.frm_from16.proPage.value = '';
	document.frm_from16.minLimit.value = '';
	document.frm_from16.emp_id.value = '';
	document.frm_from16.formType.value = '';
	document.frm_from16.submit();
} */

function loadMore(proPage, minLimit) {
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var strSearch = document.getElementById("strSearch").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paramValues = "";
		paramValues = '&f_org'+org+'&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
			+'&financialYear='+financialYear+'&strSearch='+strSearch;
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Form16.action?proPage='+proPage+'&minLimit='+minLimit+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
	
}

</script>

<script type="text/javascript">
	hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
	hs.outlineType = 'rounded-white';
	hs.wrapperClassName = 'draggable-header';
</script>

		<div class="add_delete_toolbar"></div>
		<div class="box-header with-border">
		    <h3 class="box-title">Form 16 for financial year <%=strFinancialYearStart%> to <%=strFinancialYearEnd%></h3>
		</div>
                <!-- /.box-header -->
		<div class="box-body" style="padding: 5px; min-height: 600px;">
			<s:form name="frm_from16" id="frm_from16" action="Form16" theme="simple" method="post">
				<s:hidden name="proPage" id="proPage"/>
				<s:hidden name="minLimit" id="minLimit"/>
				<s:hidden name="emp_id" id="emp_id"/>
				<s:hidden name="formType" id="formType"/>
						
				<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
					<div class="box-header with-border">
						<h3 class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
						<div class="box-tools pull-right"><button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						</div>
					</div>
					<!-- /.box-header -->
					<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
						
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true" />
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">SBU</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true" />
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
								</div>
							</div>
						</div>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" onchange="submitForm('2');" list="financialYearList" key="" />
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
								</div>
							</div>
						</div>
					
					</div>
					<!-- /.box-body -->
				</div>
		
				<div class="clr alignCenter">
			        <span>Search:</span>
			        <input type="text" id="strSearch" name="strSearch" value="<%=uF.showData(strSearch,"") %>"/> 
			        <input type="button" value="Search" class="btn btn-primary" onclick="submitForm('3');"/>
			    </div>
			    <script type="text/javascript">
					$("#strSearch").autocomplete({
						source: [ <%=uF.showData(sbData,"") %> ]
					});
				</script>
			</s:form>
			
			<div class="clr margintop20">
				<table  id="lt" class="table table-bordered">
					<tr>
						<td class="reportHeading">Employee Name</td>
						<td class="reportHeading">Financial Year</td>
						<td class="reportHeading">&nbsp;</td>
						<td class="reportHeading">Action</td>
					</tr> 
				<%
		 			Iterator<String> it = hmEmployeeMap.keySet().iterator();
		 			while (it.hasNext()) {
		 				String strEmpId = (String) it.next();
		 		%>
						<tr>
							<td class="reportLabel"><%=(String) hmEmployeeMap.get(strEmpId)%></td>
							<td class="reportLabel"><%=strFinancialYearStart%> - <%=strFinancialYearEnd%> </td>
							<td class="reportLabel">
								<%if(hmEmpForm16Status.containsKey(strEmpId)){
									Map<String, String> hmEmpForm16 = hmEmpForm16Status.get(strEmpId);
									if(hmEmpForm16 == null) hmEmpForm16 = new HashMap<String, String>();
								%>
									<a target="blank" class="fa fa-file-pdf-o" href="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_FORM16+"/"+strEmpId+"/"+hmEmpForm16.get("FORM16_NAME")%>" title="Download Form16"></a>
								<%} else { %>
									<a href="javascript:void(0)" onclick="return hs.htmlExpand(this);">View</a>
									<a href="javascript:void(0)" title="Generate Form 16" class="fa fa-file-pdf-o" onclick="generatePdf('<%=strEmpId%>')"></a>
								
									<div class="highslide-maincontent">
										<h3>Form 16 for <%=(String) hmEmployeeMap.get(strEmpId)%></h3>
										<jsp:include page="Form16Form.jsp">
											<jsp:param value="<%=strEmpId%>" name="strEmpId"/>
											<jsp:param value="<%=hmEmpSlabType.get(strEmpId) %>" name="slabType"/>
											<jsp:param value="<%=strFinancialYearStart%>" name="strFinancialYearStart"/>
											<jsp:param value="<%=strFinancialYearEnd%>" name="strFinancialYearEnd"/>
										</jsp:include>
									</div> 
								<%} %>
							</td>
							<td class="reportLabel">
								<%if(hmEmpForm16Status.containsKey(strEmpId)){
									Map<String, String> hmEmpForm16 = hmEmpForm16Status.get(strEmpId);
									if(hmEmpForm16 == null) hmEmpForm16 = new HashMap<String, String>();
								%>
									<img style="margin-left: 4px;" onclick="revokeForm16('<%=strEmpId%>','<%=(String) hmEmployeeMap.get(strEmpId)%>')" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>&nbsp;
									Approved and released by <%=uF.showData(hmEmpForm16.get("APPROVED_BY"),"") %> on <%=uF.showData(hmEmpForm16.get("APPROVED_DATE"),"") %>
								<%} else { %>
									<input type="button" value="Approve and Release" class="btn btn-primary" onclick="approveAndRelease('<%=strEmpId%>','<%=(String) hmEmployeeMap.get(strEmpId)%>')"/>
								<%} %>
							</td>
						</tr>
						<%}%>
						<%if(hmEmployeeMap.size() == 0) { %>
							<tr><td class="reportLabel alignCenter" colspan="5">No employee found in this financial year</td></tr>
						<%}%>
				</table>
			</div>
						
			<div style="text-align: center; float: left; width: 100%;">
				<%
					int intPageCnt = uF.parseToInt(pageCount);
					int pageCnt = 0;
					int minLimit = 0;
					
					for(int i=1; i<=intPageCnt; i++) { 
						minLimit = pageCnt * 10;
						pageCnt++;
						if(i ==1) {
							String strPgCnt = (String)request.getAttribute("proPage");
							String strMinLimit = (String)request.getAttribute("minLimit");
							if(uF.parseToInt(strPgCnt) > 1) {
								 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
								 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
							}
							if(strMinLimit == null) {
								strMinLimit = "0";
							}
							if(strPgCnt == null) {
								strPgCnt = "1";
							}
				%>
							<span style="color: lightgray;">
							<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
								<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');">
								<%="< Prev" %></a>
							<% } else { %>
								<b><%="< Prev" %></b>
							<% } %>
							</span>
							<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
							<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
							style="color: black;"<% } %>><%=pageCnt %></a></span>
							
							<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
								<b>...</b>
							<% } %>
				
				<% } %>
				
				<% if(i > 1 && i < intPageCnt) { %>
					<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
						<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
						<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
						style="color: black;"<% } %>><%=pageCnt %></a></span>
					<% } %>
				<% } %>
				
				<% if(i == intPageCnt && intPageCnt > 1) {
					String strPgCnt = (String)request.getAttribute("proPage");
					String strMinLimit = (String)request.getAttribute("minLimit");
					 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
					 strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
					 if(strMinLimit == null) {
						strMinLimit = "0";
					}
					if(strPgCnt == null) {
						strPgCnt = "1";
					}
					%>
					<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intPageCnt) { %>
						<b>...</b>
					<% } %>
				
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"<% } %>><%=pageCnt %></a></span>
					<span style="color: lightgray;">
					<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
						<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');"><%="Next >" %></a>
					<% } else { %>
						<b><%="Next >" %></b>
					<% } %>
					</span>
				<% } %>
				<%} %>
			</div>
						
		</div>
		<!-- /.box-body -->
</div>
