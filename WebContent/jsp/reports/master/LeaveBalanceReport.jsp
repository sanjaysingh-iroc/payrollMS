<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<div id="divResult">

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#lt').DataTable({
		aLengthMenu: [
			  			[25, 50, 100, 200, -1],
			  			[25, 50, 100, 200, "All"]
			  		],
		iDisplayLength: -1,
		dom: 'lBfrtip',
		"ordering": false,
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print' 
        ]
	});
	$("#f_wLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
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
});

function generateReportExcel(){
	window.location = "ExportExcelReport.action";
}

function submitForm(type){
	document.frm.exportType.value='';
	var strSearch = document.getElementById("strSearch").value;
	var org = document.getElementById("f_org").value;
	var calendarYear = document.getElementById("calendarYear").value;
	var location = getSelectedValue("f_wLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
		+'&strLevel='+level+'&calendarYear='+calendarYear;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'LeaveBalanceReport.action?strSearch='+strSearch+'&f_org='+org+paramValues, 
		data: $("#"+this.id).serialize(),
		success: function(result){
        	//console.log(result);
        	$("#divResult").html(result);
   		}
	});
}

function loadMore(proPage, minLimit) {
	var strSearch = document.getElementById("strSearch").value;
	var org = document.getElementById("f_org").value;
	var calendarYear = document.getElementById("calendarYear").value;
	var location = getSelectedValue("f_wLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
		+'&strLevel='+level+'&calendarYear='+calendarYear;

	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'LeaveBalanceReport.action?strSearch='+strSearch+'&f_org='+org+'&proPage='+proPage+'&minLimit='+minLimit+paramValues,
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

</script>

<%
  UtilityFunctions uF = new UtilityFunctions();
  String strTitle = (String)request.getAttribute(IConstants.TITLE); 
  List<String> almonth = (List<String>)request.getAttribute("almonth"); 
  if(almonth == null) almonth = new ArrayList<String>();
  String strYear = (String)request.getAttribute("strYear");
  String strDate = "";
  if(uF.parseToInt(strYear) > 0){
	  strDate = "01/01/"+strYear;
  }
  
  String pageCount = (String)request.getAttribute("pageCount");
  String sbData = (String) request.getAttribute("sbData");
  String strSearch = (String) request.getAttribute("strSearch");
  
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
                    <s:form name="frm" action="LeaveBalanceReport" theme="simple" method="post">
                        <s:hidden name="exportType"></s:hidden>
                        <div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
                                	<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
                                	<s:select name="f_wLocation" id="f_wLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
                                	<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
								</div>

								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">SBU</p>
                                	<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"/>
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
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Calendar Year</p>
									<s:select label="Select Calendar Year" name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" 
									headerKey="0" onchange="submitForm('2');" list="calendarYearList" key=""/>
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
        
        <div class="col-lg-12 col-md-12 col-sm-12 no-padding" style="text-align: center;">
			<%-- <span>Search:</span> --%>
			<input type="text" id="strSearch" class="form-control" name="strSearch" placeholder="Search" value="<%=uF.showData(strSearch, "") %>"/>
			<input type="button" value="Search" class="btn btn-primary" onclick="submitForm('2');">
		</div>
		
		<script type="text/javascript">
			$("#strSearch").autocomplete({
				source: [ <%=uF.showData(sbData,"") %> ]
			});
		</script>
		
		<div style="text-align: center; float: left; width: 100%; padding-top: 15px; padding-bottom: 15px;">
			<%
			int intPageCnt = uF.parseToInt(pageCount);
				int pageCnt = 0;
				int minLimit = 0;
				
				for(int i=1; i<=intPageCnt; i++) { 
					minLimit = pageCnt * 10;
					pageCnt++;
			%>
			<% if(i ==1) {
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

		<div>
			<display:table name="reportList" class="table table-bordered" id="lt">
			    <display:column style="text-align:center;" nowrap="nowrap" title="Employee Code" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Employee Name" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Department" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Manager" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Types of Leave" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Date of Birth" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Date of Joining" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
				<%
					int nAlMonth = almonth.size();
					for (int ii=0; ii < nAlMonth; ii++){
						int count = 7+ii;
						String strValue = almonth.get(ii).toString();
				%>
						<display:column style="text-align:right;" title="<%=strValue%>" nowrap="nowrap"><%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
				<%
					}  
					int nTotalAvail = (7+nAlMonth);
					int nTotalBalance = (8+nAlMonth);
				%>
				<display:column style="text-align:right;" title="Total Leave Availed" sort="false" nowrap="nowrap"><%=((java.util.List) pageContext.getAttribute("lt")).get(nTotalAvail)%></display:column>
				<display:column style="text-align:right;" title="Total Leave Balance" sort="false" nowrap="nowrap"><%=((java.util.List) pageContext.getAttribute("lt")).get(nTotalBalance)%></display:column>					
			</display:table>
		</div>
	</div>
</div>

