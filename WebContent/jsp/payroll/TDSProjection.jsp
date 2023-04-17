<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	Map hmEmpMap = (Map) request.getAttribute("hmEmpMap");
	Map hmEmpCodeMap = (Map) request.getAttribute("hmEmpCodeMap");
	Map hmTDSEmp = (Map) request.getAttribute("hmTDSEmp");
	Map hmTDSPaidEmp = (Map) request.getAttribute("hmTDSPaidEmp");
	//Map<String, String> hmPrevOrgTDSDetails = (Map<String, String>) request.getAttribute("hmPrevOrgTDSDetails");
	Map<String, Map<String, String>> hmPrevOrgTDSDetailsAllEmp = (Map<String, Map<String, String>>) request.getAttribute("hmPrevOrgTDSDetailsAllEmp");
	if(hmPrevOrgTDSDetailsAllEmp==null) hmPrevOrgTDSDetailsAllEmp= new HashMap<String, Map<String, String>>();
	
	/* Map hmTDSProjectedEmp1 = (Map)request.getAttribute("hmTDSProjectedEmp1"); */

	List alEmp = (List) request.getAttribute("alEmp");
	List alMonth = (List) request.getAttribute("alMonth");

	List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
	List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
	
	String pageCount = (String)request.getAttribute("pageCount");

	String sbData = (String) request.getAttribute("sbData");
	String strSearch = (String) request.getAttribute("strSearch");
%>

<script type="text/javascript" src="scripts/customAjax.js"></script>

<script type="text/javascript" charset="utf-8">
			
function selectall(x,strEmpId){
 	var  status=x.checked;
	var  arr= document.getElementsByName(strEmpId);
	for(i=0;i<arr.length;i++){
  		arr[i].checked=status;
 	}
}

function exportpdf(){
	window.location="ExportExcelReport.action";
}

function deleteTDS(divId,month,empId,financialYear){
	//alert('sdfgdsfg');
	if(confirm('Are you sure you want to delete this entry?')){
		var action = 'TDSProjection.action?action=D&month='+month+'&empId='+empId+'&financialYear='+financialYear;
		getContent(divId,action);
	}
}

function loadMore(proPage, minLimit) {
	document.frm_TDSPayroll.proPage.value = proPage;
	document.frm_TDSPayroll.minLimit.value = minLimit;
	//document.frm_TDSPayroll.submit();
	
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var f_paymentMode = document.getElementById("f_paymentMode").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var level = getSelectedValue("f_level");
	var strGrade = getSelectedValue("f_grade");
	var strEmployeType = getSelectedValue("f_employeType");
	
	var strSearch = document.getElementById("strSearch").value;
	
	var paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&financialYear='+financialYear
			+'&f_paymentMode='+f_paymentMode+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType+'&strSearch='+strSearch;
	
	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	var form_data = $("#"+this.id).serialize();
	$.ajax({ 
		type : 'POST',
		url: 'TDSProjection.action?proPage='+proPage+'&minLimit='+minLimit+'&f_org='+org+paramValues,
		data: form_data,
		cache: true,
		success: function(result){
			$("#subDivResult").html(result);
   		}
	});
}

</script>

<script type="text/javascript">

$(function(){
	/* $('#lt').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
  	}); */
	
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	//$("#f_paymentMode").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_employeType").multiselect().multiselectfilter();
	$("#f_grade").multiselect().multiselectfilter();
});    


$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
 });
 
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var f_paymentMode = document.getElementById("f_paymentMode").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var level = getSelectedValue("f_level");
	var strGrade = getSelectedValue("f_grade");
	var strEmployeType = getSelectedValue("f_employeType");
	
	var strSearch = document.getElementById("strSearch").value;
	
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&financialYear='+financialYear
			+'&f_paymentMode='+f_paymentMode+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType;
	} else if(type == '3'){
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&financialYear='+financialYear
		+'&f_paymentMode='+f_paymentMode+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType+'&strSearch='+strSearch;
	}else if(type=='4'){
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&financialYear='+financialYear
		+'&f_paymentMode='+f_paymentMode+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType+'&exceldownload=true';
	}
	
	//alert("service ===>> " + service);
	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'TDSProjection.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#subDivResult").html(result);
   		}
	});
}


function downloadForm() {
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var f_paymentMode = document.getElementById("f_paymentMode").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var level = getSelectedValue("f_level");
	var strGrade = getSelectedValue("f_grade");
	var strEmployeType = getSelectedValue("f_employeType");
	var strSearch = document.getElementById("strSearch").value;
	
	var paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&financialYear='+financialYear
		+'&f_paymentMode='+f_paymentMode+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType+'&exceldownload=true';
	window.location='TDSProjection.action?f_org='+org+paramValues;
	//alert("service ===>> " + service);
	
}

function submit(type){
	if(type=='3')
		{
		document.frm_TDSPayroll.exceldownload.value = 'true';
		}else{
			document.frm_TDSPayroll.exceldownload.value = 'false';
		}
	document.frm_TDSPayroll.submit();
}

function getLevelwiseGrade() {
	
	var orgId = document.getElementById("f_org").value;
	var levelIds = getSelectedValue('f_level');
	var xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
	} else {
		var xhr = $.ajax({
			url : 'GetGradeList.action?fromPage=filter&orgId='+orgId+'&levelIds='+levelIds,
			cache : false,
			success : function(data) {
				
				document.getElementById('myGrade').innerHTML = data;
				$("#f_grade").multiselect().multiselectfilter();
			}
		});
	}
}


	function GetXmlHttpObject() {
	    if (window.XMLHttpRequest) {
	        // code for IE7+, Firefox, Chrome, Opera, Safari
	        return new XMLHttpRequest();
	    }
	    if (window.ActiveXObject) {
	    	// code for IE6, IE5
	        return new ActiveXObject("Microsoft.XMLHTTP");
	    }
	    return null;
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

function importTdsProjection(){ 
	
	var strgrade = getSelectedValue("f_grade");
	var strLevel = getSelectedValue("f_level");
	var strdepartment = getSelectedValue("f_department");
	var strLocation = getSelectedValue("f_strWLocation");
	var strorg = document.getElementById("f_org").value;
	var strpaymentmode = document.getElementById("f_paymentMode").value;
	var financialYear = document.getElementById("financialYear").value;
 	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Import TDS Projection');
	 $.ajax({
		 
		url : "ImportTDSProjection.action?strorg="+strorg+"&strgrade="+strgrade+"&strLevel="+strLevel+"&strdepartment="+strdepartment+"&strLocation="+strLocation+"&strpaymentmode="+strpaymentmode+"&financialYear="+financialYear,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="TDS Projection" name="title"/>
</jsp:include> --%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
	
	
	<%-- <s:form theme="simple" name="frm_ImportTDSProjection" id="frm_ImportTDSProjection" action="ImportTDSProjection" enctype="multipart/form-data" method="POST">
					<s:hidden name="financialYear"></s:hidden>
					<table style="width:100%">
						<tbody>
							<tr>
								<td class="txtlabel alignRight">Select File to Import</td>
								<td><input type="file" id="ImportEmployees_fileUpload" value="" size="20" name="fileUpload"></td>
							</tr>  
							<tr>
								<td align="center" colspan="2"><input type="submit" class="input_button" value="Import File" id="ImportEmployees_0"/>
								</td>
							</tr>
							<tr>
							</tr>
						</tbody>
					</table>
	</s:form> --%>
	
		<div id="printDiv" class="leftbox reportWidth">
		
		<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
                        <%session.setAttribute(IConstants.MESSAGE,""); %>
			<s:form name="frm_TDSPayroll" action="TDSProjection" theme="simple" method="post">
				<s:hidden name="proPage" id="proPage"/>
				<s:hidden name="minLimit" id="minLimit"/>
				<div class="box box-default collapsed-box" >
	                <div class="box-header with-border">
	                   <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
	                    <div class="box-tools pull-right">
	                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
	                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                    </div>
	                </div>
	                <!-- /.box-header -->
	                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
	                    <div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select theme="simple" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" onchange="submitForm('2');" list="strFinancialYearList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organisation</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName"	onchange="submitForm('1');" list="organisationList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true"  onchange="getLevelwiseGrade();" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Payment Mode</p>
									<s:select theme="simple" name="f_paymentMode" id="f_paymentMode" listKey="payModeId" listValue="payModeName" list="paymentModeList" key="" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
                                <p style="padding-left: 5px;">Grade</p>
                                <div id="myGrade">
                                	<s:select theme="simple" name="f_grade" id="f_grade" list="gradeList" listKey="gradeId" listValue="gradeCode" key="" multiple="true"  />
                                </div>
                        		</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
								</div>
							</div>
						</div>
	                </div>
	                <!-- /.box-body -->
	            </div>
	            
				<div class="col-md-12" style="margin: 0px 0px 10px 0px; text-align: right;">
                      <div style="float: left;line-height: 22px; width: 514px; margin-left: 350px;">
                          <span style="float: left; display: block; width: 78px;">Search:</span>
                          <div style="margin: 0px 0px 0px 16px; float: left; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
                              <div style="float: left">
                                  <input type="text" name="strSearch" id="strSearch" class="form-control" style="margin-left: 0px; width: 250px; box-shadow: 0px 0px 0px #ccc" 
                                  	value="<%=uF.showData(strSearch,"") %>" />
                              </div>
                              <div style="float: right">
                                  <input type="button" value="Search" class="btn btn-primary" name="submit" onclick="submitForm('3');" style="margin-left: 10px;"/>
                              </div>
                          </div>
                      </div>
                      <script>
                          $( "#strSearch" ).autocomplete({
                          	source: [ <%=uF.showData(sbData,"") %> ]
                          });
                      </script>
                </div>
				
				<!-- <div class="row row_without_margin">
					<input type="hidden" id="exceldownload" name="exceldownload" value ="false">
					<p style="font-size: 14px;" class="pull-right">
						<a href="javascript:void(0)" title="Export to Excel" class="excel pull-right" onclick="downloadForm();"></a>
					</p>
				</div> -->
	
				<div class="clr margintop20">
				<div style="text-align: right;"> 
					<a href="javascript:void(0)" onclick="importTdsProjection();">Import TDS Projection</a>
				</div>
					<table class="table table-bordered overflowtable" id="lt">
						<thead>
							<tr>
								<th class="alignCenter" nowrap="nowrap">Employee Code</th>
								<th class="alignCenter" nowrap="nowrap">Employee Name</th>
								<%
									alInnerExport.add(new DataStyle("TDS Projection for "+ request.getAttribute("strD1") + " - "+ request.getAttribute("strD2"), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle("Employee Code",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
								%>
								<% for (int j = 0; j < alMonth.size(); j++) { %>
								<th class="" nowrap="nowrap"><%=uF.getMonth(uF.parseToInt((String) alMonth.get(j)))%></th>
								<% alInnerExport.add(new DataStyle(uF.getMonth(uF.parseToInt((String) alMonth.get(j))),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY)); %>
								<% } %>
							</tr>
							<% reportListExport.add(alInnerExport); %>
						
						<thead>
						<tbody>
	
	
							<% int i = 0;
								String financialYear = (String) request.getAttribute("financialYear");
								String[] tmpFY = financialYear.split("-");
								String fyStart = tmpFY[0];
								String fyEnd = tmpFY[1];
								String fyStartMonth = uF.getDateFormat(fyStart+"", IConstants.DATE_FORMAT, "MM");
								String ftStartYr = uF.getDateFormat(fyStart+"", IConstants.DATE_FORMAT, "yyyy");
								
								String fyEndMonth = uF.getDateFormat(fyEnd+"", IConstants.DATE_FORMAT, "MM");
								String ftEndYr = uF.getDateFormat(fyEnd+"", IConstants.DATE_FORMAT, "yyyy");
								
								//System.out.println("jsp hmPrevOrgTDSDetailsAllEmp ===>> " + hmPrevOrgTDSDetailsAllEmp +" -- ftStartYr ===>> " + ftStartYr + " -- ftEndYr ===>> " + ftEndYr);
								for (i = 0; alEmp != null && i < alEmp.size(); i++) {
									Map<String, String> hmPrevOrgTDSDetails = hmPrevOrgTDSDetailsAllEmp.get((String) alEmp.get(i));
									if(hmPrevOrgTDSDetails==null) hmPrevOrgTDSDetails = new HashMap<String, String>();
									String strEmpJoiningMonth = null;
									String strEmpJoiningYr = null;
									if(hmPrevOrgTDSDetails!=null && hmPrevOrgTDSDetails.size()>0) {
										strEmpJoiningMonth = hmPrevOrgTDSDetails.get((String) alEmp.get(i)+"_JOINING_MONTH");
										strEmpJoiningYr = hmPrevOrgTDSDetails.get((String) alEmp.get(i)+"_JOINING_YEAR");
									}
									//System.out.println((String) alEmp.get(i) + " -- jsp strEmpJoiningMonth ===>> " + strEmpJoiningMonth+ " -- strEmpJoiningYr ===>> " + strEmpJoiningYr);
							%>
							<tr>
								<td class="" nowrap="nowrap"><%=(String) hmEmpCodeMap.get((String) alEmp.get(i))%></td>
								<td class="" nowrap="nowrap"><%=(String) hmEmpMap.get((String) alEmp.get(i))%></td>
								<%
									alInnerExport = new ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle((String) hmEmpCodeMap.get((String) alEmp.get(i)), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle((String) hmEmpMap.get((String) alEmp.get(i)), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								%>
								<% 
								//System.out.println("strEmpJoiningMonth ===>> " + strEmpJoiningMonth);
								for (int j = 0; j < alMonth.size(); j++) {%>
								<td nowrap="nowrap">
									<% if (uF.parseToInt(strEmpJoiningMonth)>0 && ((uF.parseToInt(strEmpJoiningMonth)>3 && uF.parseToInt(strEmpJoiningMonth) > uF.parseToInt((String)alMonth.get(j)) && uF.parseToInt((String)alMonth.get(j))>3) ||
										(uF.parseToInt(strEmpJoiningMonth)<=3 && uF.parseToInt(strEmpJoiningMonth) < uF.parseToInt((String)alMonth.get(j)) && uF.parseToInt((String)alMonth.get(j))>3) ||
										(uF.parseToInt(strEmpJoiningMonth)<=3 && uF.parseToInt(strEmpJoiningMonth) > uF.parseToInt((String)alMonth.get(j)) && uF.parseToInt(ftEndYr) == uF.parseToInt(strEmpJoiningYr)) ) ) { %>
									<div id="id_<%=alMonth.get(j)%>_<%=alEmp.get(i)%>"><%="N/A"%></div>
									<%
										alInnerExport.add(new DataStyle(""+ hmTDSPaidEmp.get((String) alEmp.get(i)+ "_" + (String) alMonth.get(j)),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0","0", BaseColor.LIGHT_GRAY));
										} else if (hmTDSPaidEmp.containsKey((String) alEmp.get(i)+ "_" + (String) alMonth.get(j))) { %>
									<div id="id_<%=alMonth.get(j)%>_<%=alEmp.get(i)%>"><%=hmTDSPaidEmp.get((String) alEmp.get(i)+ "_" + (String) alMonth.get(j))%></div>
									<%
										alInnerExport.add(new DataStyle(""+ hmTDSPaidEmp.get((String) alEmp.get(i)+ "_" + (String) alMonth.get(j)),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0","0", BaseColor.LIGHT_GRAY));
										} else if (j < alMonth.size() - 1) {
									%> <%-- <%=hmTDSEmp %> --%>
									<div style="float: left">
										<input type="hidden" name="strEmpId_strMonth" value="<%=(String) alEmp.get(i)%>_<%=(String) alMonth.get(j)%>" />
										<input id="amt_<%=(String) alEmp.get(i) + "_" + (String) alMonth.get(j)%>" type="text" name="strTDS" value="<%=uF.showData((String) hmTDSEmp.get((String) alEmp.get(i)+ "_" + (String) alMonth.get(j)),"0")%>" style="width:75px !important; text-align: right;" />
										<% alInnerExport.add(new DataStyle(uF.showData((String) hmTDSEmp.get((String) alEmp.get(i)+ "_" + (String) alMonth.get(j)),"0"), Element.ALIGN_CENTER, "NEW_ROMAN", 6,"0", "0", BaseColor.LIGHT_GRAY)); %>
									</div>
									
									<div style="float: left" id="id_<%=alMonth.get(j)%>_<%=alEmp.get(i)%>"></div> 
									<a href="javascript:void()" onclick="getContent('id_<%=alMonth.get(j)%>_<%=alEmp.get(i)%>', 'TDSProjection.action?action=U&month=<%=(String) alMonth.get(j)%>&empId=<%=(String) alEmp.get(i)%>&financialYear=<%=request.getAttribute("financialYear")%>&strAmount='+document.getElementById('amt_<%=(String) alEmp.get(i) + "_"+ (String) alMonth.get(j)%>').value)">
										<i class="fa fa-pencil-square-o" aria-hidden="true" title="click to update"></i>
									</a> 
									<a href="javascript:void()" onclick="deleteTDS('id_<%=alMonth.get(j)%>_<%=alEmp.get(i)%>','<%=(String) alMonth.get(j)%>','<%=(String) alEmp.get(i)%>','<%=request.getAttribute("financialYear")%>')">
										<i class="fa fa-trash" aria-hidden="true" title="click to remove" ></i>
									</a>
									<div style="width: 120px;"></div> 
									<% } else if (j == alMonth.size() - 1) { %>
									<div style="float: left">
										<input type="hidden" name="strEmpId_strMonth" value="<%=(String) alEmp.get(i)%>_<%=(String) alMonth.get(j)%>" />
										<input id="amt_<%=(String) alEmp.get(i) + "_"+ (String) alMonth.get(j)%>" type="text" name="strTDS" value="<%=uF.showData((String) hmTDSEmp.get((String) alEmp.get(i)+ "_" + (String) alMonth.get(j)),"0")%>" style="width:75px !important; text-align: right;" />
										<% alInnerExport.add(new DataStyle(uF.showData((String) hmTDSEmp.get((String) alEmp.get(i)+ "_" + (String) alMonth.get(j)),"0"), Element.ALIGN_CENTER, "NEW_ROMAN", 6,"0", "0", BaseColor.LIGHT_GRAY)); %>
									</div>
									<div style="float: left" id="id_<%=alMonth.get(j)%>_<%=alEmp.get(i)%>"></div> 
									<a href="javascript:void()" onclick="getContent('id_<%=alMonth.get(j)%>_<%=alEmp.get(i)%>', 'TDSProjection.action?action=U&month=<%=(String) alMonth.get(j)%>&empId=<%=(String) alEmp.get(i)%>&financialYear=<%=request.getAttribute("financialYear")%>&strAmount='+document.getElementById('amt_<%=(String) alEmp.get(i) + "_"+ (String) alMonth.get(j)%>').value)">
										<i class="fa fa-pencil-square-o" aria-hidden="true" title="click to update"></i>
									</a> 
									<a href="javascript:void()" onclick="deleteTDS('id_<%=alMonth.get(j)%>_<%=alEmp.get(i)%>','<%=(String) alMonth.get(j)%>','<%=(String) alEmp.get(i)%>','<%=request.getAttribute("financialYear")%>')">
										<i class="fa fa-trash" aria-hidden="true" title="click to remove" ></i>
									</a>
									<div style="width: 120px;"></div> 
									<% } %>
								</td>
								<% } %>
							</tr>
							<% reportListExport.add(alInnerExport);
							}
							%>
						</tbody>
					</table>
	
					<sup>*</sup> The amount shown is the projected amount and not the actual amount. TDS will be calculated on actual amount paid and
					will consider all the exemptions/investments while processing the salary for this month.
					<!-- <input type="submit" name="submit" value="Submit" /> -->
	
				</div>
				<% if (i == 0) { %>
				<div class="filter">
					<div class="msg nodata"><span> No data available for the current selection </span></div>
				</div>
				<% }
				session.setAttribute("reportListExport", reportListExport);
				%>
				<div style="text-align: center; float: left; width: 100%;">
					<%
					int intPageCnt = uF.parseToInt(pageCount);
						int pageCnt = 0;
						int minLimit = 0;
						
						for(i=1; i<=intPageCnt; i++) { 
							minLimit = pageCnt * 15;
							pageCnt++;
					%>
					<% if(i ==1) {
						String strPgCnt = (String)request.getAttribute("proPage");
						String strMinLimit = (String)request.getAttribute("minLimit");
						if(uF.parseToInt(strPgCnt) > 1) {
							 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
							 strMinLimit = (uF.parseToInt(strMinLimit)-15) + "";
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
						 strMinLimit = (uF.parseToInt(strMinLimit)+15) + "";
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
			</s:form>
		</div>
	</div>
	
	
	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">-</h4>
	            </div>
	            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>
	
	<!-- /.box-body -->

