<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">
function generateSalaryExcel(){
	window.location="ExportExcelReport.action?type=type";
}

$(function() {
	$('#lt1').DataTable({ 
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	
    $("#strStartDate").datepicker({format: 'dd/mm/yyyy'});
    $("#strEndDate").datepicker({format: 'dd/mm/yyyy'});
    
    var value = document.getElementById("selectOne").value;
    checkSelectType(value);
});


function checkSelectType(value) {
	//fromToDIV financialYearDIV monthDIV paycycleDIV
	if(value == '1') {
		document.getElementById("fromToDIV").style.display = 'block';
		document.getElementById("financialYearDIV").style.display = 'none';
		document.getElementById("monthDIV").style.display = 'none';
	} else if(value == '2') {
		document.getElementById("fromToDIV").style.display = 'none';
		document.getElementById("financialYearDIV").style.display = 'block';
		document.getElementById("monthDIV").style.display = 'none';
	} else if(value == '3') {
		document.getElementById("fromToDIV").style.display = 'none';
		document.getElementById("financialYearDIV").style.display = 'none';
		document.getElementById("monthDIV").style.display = 'block';
	} else if(value == '4') {
		document.getElementById("fromToDIV").style.display = 'none';
		document.getElementById("financialYearDIV").style.display = 'none';
		document.getElementById("monthDIV").style.display = 'none';
	} else {
		document.getElementById("strStartDate").value = '';
		document.getElementById("strEndDate").value = '';
		document.getElementById("fromToDIV").style.display = 'none';
		document.getElementById("financialYearDIV").style.display = 'none';
		document.getElementById("monthDIV").style.display = 'none';
	}
}


function submitForm(type){
	document.frmMyselfUtilizationReport.exportType.value='';
	var data = $("#frmMyselfUtilizationReport").serialize();
	//alert("data ===>> " + data);
	$("#actionResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'MyselfUtilizationReport.action',
		data: data,
		success: function(result){
        	$("#actionResult").html(result);
        	$("#f_client").multiselect().multiselectfilter();
        	$("#f_project").multiselect().multiselectfilter();
   		}
	});
}

function getProjectsByClient() {
	var xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
	} else {
		var strClient = getSelectedValue("f_client");
		
		var xhr = $.ajax({
			url : 'GetProjects.action?strClient='+strClient,
			cache : false,
			success : function(data) {
				document.getElementById('projectDiv').innerHTML = data;
				$("#f_project").multiselect().multiselectfilter();
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

$(function() {
	$("#f_client").multiselect().multiselectfilter();
	$("#f_project").multiselect().multiselectfilter();
});

</script>



<%
	UtilityFunctions uF = new UtilityFunctions();
	List<List<String>> alOuter = (List<List<String>>)request.getAttribute("alOuter");
	boolean poFlag = (Boolean) request.getAttribute("poFlag");
%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="desgn" style="margin-bottom: 5px;color:#232323;">
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
                    <s:form name="frmMyselfUtilizationReport" id="frmMyselfUtilizationReport" action="MyselfUtilizationReport" theme="simple">
                        <s:hidden name="exportType"></s:hidden>
                        <div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Client</p>
									<s:select name="f_client" id="f_client" listKey="clientId" listValue="clientName" list="clientList" key="" multiple="true" onchange="getProjectsByClient();"/>
			             		</div>
			             		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Project</p>
									<div id="projectDiv">
										<s:select name="f_project" id="f_project" listKey="id" listValue="name" list="projectList" key="" multiple="true"/>
									</div>
			             		</div>
							</div>
						</div>
						
						<div class="row row_without_margin" style="margin-top: 10px;">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Select Period</p> 
									<s:select theme="simple" name="selectOne" id="selectOne" cssStyle="float:left; margin-right: 10px;" headerKey="" 
										headerValue="Today" list="#{'1':'From-To', '2':'Financial Year', '3':'Month'}" onchange="checkSelectType(this.value);"/> <!--   -->
								</div>
								
								<div id="fromToDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display: none;">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="text" name="strStartDate" id="strStartDate" placeholder="From Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strStartDate"), "") %>"/>
									<input type="text" name="strEndDate" id="strEndDate" placeholder="To Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strEndDate"), "") %>"/>
					      		</div>
					      		
					      		<div id="financialYearDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display: none;">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select name="financialYear" listKey="financialYearId" listValue="financialYearName" headerValue="Select Financial Year" list="financialYearList" />
					      		</div>
					      		
					      		<div id="monthDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display: none;">
									<p style="padding-left: 5px;">Financial Year &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Month</p>
									<s:select name="monthFinancialYear" listKey="financialYearId" listValue="financialYearName" headerValue="Select Financial Year" list="financialYearList" /> 
									<s:select name="strMonth" cssStyle="margin-left: 7px; width: 100px !important;" listKey="monthId" listValue="monthName" list="monthList" />	
					      		</div>
					      		
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div>
						</div>
                    </s:form>
                </div>
                <!-- /.box-body -->
            </div>
        </div>
     

		<display:table name="alOuter" cellspacing="1" class="table table-bordered" id="lt1">
			<display:column style="text-align:left; width: 25%;" valign="top" title="Task Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Planned Hrs"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>	
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Actual Hrs"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>	
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title=" % utilization"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
		</display:table>

	</div>
