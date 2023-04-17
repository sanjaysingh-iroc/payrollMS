<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
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
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_grade").multiselect().multiselectfilter();
	$("#f_employeType").multiselect().multiselectfilter();
	
	$("#strStartDate").datepicker({format : 'dd/mm/yyyy'});	
});

function generateReportExcel(){
	window.location = "ExportExcelReport.action";
}

function submitForm(type){
	/* document.frm.exportType.value=''; */
	
	var paycycle = document.getElementById("paycycle").value;
	var org = document.getElementById("f_org").value;
	/* var strStartDate = document.getElementById("strStartDate").value; */
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strGrade = getSelectedValue("f_grade");
	var strEmployeType = getSelectedValue("f_employeType");
	var paramValues = "";
	
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
		+'&strLevel='+level+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType+'&paycycle='+paycycle;
	}
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'DailyOverTimeReport.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
			
        	console.log(result);
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

function loadMore(pageNumber, minLimit) {
	
	var paycycle = document.getElementById("paycycle").value;
	var org = document.getElementById("f_org").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strGrade = getSelectedValue("f_grade");	
	var strEmployeType = getSelectedValue("f_employeType");	
	var paramValues = "";
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
		+'&strLevel='+level+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType+'&paycycle='+paycycle+'&minLimit='+minLimit+'&pageNumber='+pageNumber;
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'DailyOverTimeReport.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
			
        	console.log(result);
        	$("#divResult").html(result);
   		}
	});
}

function searchEmp(){
	
	var strSearch = document.getElementById("strSearch").value;
	var paycycle = document.getElementById("paycycle").value;
	var org = document.getElementById("f_org").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strGrade = getSelectedValue("f_grade");
	var strEmployeType = getSelectedValue("f_employeType");
	var paramValues = "";
	
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
		+'&strLevel='+level+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType+'&paycycle='+paycycle+'&strSearch='+strSearch;
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'DailyOverTimeReport.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
			
        	console.log(result);
        	$("#divResult").html(result);
   		}
	});
}

</script>


<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

String  strYear = (String)request.getAttribute("strYear");
String  strMonth = (String)request.getAttribute("strMonth");

List<String> alDates = (List<String>)request.getAttribute("alDates"); 
List<String> alEmployees = (List<String>)request.getAttribute("alEmployees");
Map hmEmpName = (Map)request.getAttribute("hmEmpName");
Map hmEmpCodeMap =  (Map)request.getAttribute("hmEmpCodeMap");

Map<String, String> hmOTHours =(Map<String, String>)request.getAttribute("hmOTHours");
if(hmOTHours == null) hmOTHours = new HashMap<String, String>();

String sbData = (String) request.getAttribute("sbData");
String strSearch = (String) request.getAttribute("strSearch");

List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
List<DataStyle> alInnerExport = new ArrayList<DataStyle>();	
%>

 <%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include> --%>

	<!-- <div class="box-body" style="padding: 5px;  /* overflow-y: auto; */ min-height: 600px;"> -->
	 <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<s:form name="frm" action="DailyOverTimeReport" theme="simple">
        	<s:hidden name="exportType"></s:hidden>
            <s:hidden name="pageNumber" id="pageNumber"/>
			<s:hidden name="minLimit" id="minLimit"/>
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
									<p style="padding-left: 5px;">Paycycle</p>
									<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="" headerValue="Select Paycycle" onchange="submitForm('2');" list="paycycleList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
		                             		<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
		                              	<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">SBU</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" cssStyle="width:200px;" key="" multiple="true"/>								
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key=""  onchange="getLevelwiseGrade();" />
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
								<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
		                             		<s:select theme="simple" name="f_level" id="f_level"listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Grade</p>
									<s:select theme="simple" name="f_grade" id="f_grade" list="gradeList" listKey="gradeId" listValue="gradeCode" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
								</div> --%>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div>
						</div><br>
	                </div>
	                <!-- /.box-body -->
	            </div>
	        </div>
	        
	        <div style="float:left; font-size:12px; line-height:22px; width:100%; margin-left: 350px;margin-bottom: 10px;">
		         <span style="float:left; margin-right:7px;">Search:</span>
		         <div style="border:solid 1px #68AC3B; float:left; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
		           <div style="float:left">
		           	<input type="text" id="strSearch" name="strSearch" style="margin-left: 0px; border:0px solid #ccc; width:282px; box-shadow:0px 0px 0px #ccc" value="<%=uF.showData(strSearch,"") %>"/> 
		         	</div>
		        	 <div style="float:right">
		           	<!-- <input type="submit" value="Search" class="input_search"/> -->
		           	<input type="button" value="Search" class="input_search" onclick="searchEmp();"/>
		           </div>
		     	</div>
		    </div>
		      
		    <script type="text/javascript">
				$( "#strSearch" ).autocomplete({
					source: [ <%=uF.showData(sbData,"") %> ]
				});
			</script>
	        
			<br/>
			<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
				<!-- <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
				<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
			</div>
			
			<div style=" width:100%; float:left; overflow:auto; height:400px;">
			    <!-- <table cellpadding="5" cellspacing="2" class="tb_style" width="100%"> -->
			     <table name="reportList" cellspacing="1" class="table table-bordered" id="lt1" style="height:100px!important">
			     
			    	<thead>
				    	<tr>
				    		<th class="alignCenter" nowrap="nowrap">Employee Code</th>
				    		<th class="alignCenter" nowrap="nowrap">Employee Name</th>
							<%
							
							alInnerExport.add(new DataStyle("Daily Overtime report from " + request.getAttribute("startPaycycle") + " - " + request.getAttribute("endPaycycle"),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Employee Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Employee Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							
							int nDateSize = alDates != null ? alDates.size() : 0;
							for(int j=0; j < nDateSize; j++){ 
								String strDate =(String)alDates.get(j);
								alInnerExport.add(new DataStyle(strDate, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							%>
								<th class="alignCenter" nowrap="nowrap"><%=strDate %></th>
							<%}
							reportListExport.add(alInnerExport);
							%>
				    	</tr>
			    	<thead>
			    	<tbody>
			    		<%
			    		int nEmpSize = alEmployees!=null ? alEmployees.size() : 0;
			    		for(int i=0; i < nEmpSize; i++){
			    			alInnerExport = new ArrayList<DataStyle>();
	    				    alInnerExport.add(new DataStyle((String)hmEmpCodeMap.get((String)alEmployees.get(i)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
	    				    alInnerExport.add(new DataStyle((String)hmEmpName.get((String)alEmployees.get(i)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			    		%>
		    				<tr>
		    					<td class="alignCenter" nowrap="nowrap"><%=(String)hmEmpCodeMap.get((String)alEmployees.get(i)) %></td>
		    					<td class="alignLeft" nowrap="nowrap"><%=(String)hmEmpName.get((String)alEmployees.get(i)) %></td>
		    					<%for(int j=0; j<alDates.size(); j++){
		    						String strOvertime = "00:00";
		    						if(hmOTHours.get((String)alDates.get(j)+"_"+(String)alEmployees.get(i)) != null){
		    							strOvertime = hmOTHours.get((String)alDates.get(j)+"_"+(String)alEmployees.get(i));
		    						}
		    						alInnerExport.add(new DataStyle(uF.showData(strOvertime,"0:00"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		    						%>
									<td class="alignRight" nowrap="nowrap"><%=uF.showData(strOvertime,"0:00") %></td>
								<%}
		    					reportListExport.add(alInnerExport);
		    					%>
				    		</tr>
				    	<%
			    			}
			    		%>
			    	</tbody>
			    </table>
			</div>
		
			<div style="text-align: center; float: left; width: 100%;">
			 
				<% 
				session.setAttribute("reportListExport", reportListExport);
				String pageCount = (String)request.getAttribute("pageCount");
				int intproCnt = uF.parseToInt(pageCount);
					int pageCnt = 0;
					int minLimit = 0;
					
					for(int i=1; i<=intproCnt; i++) {
							minLimit = pageCnt * 50;
							pageCnt++;
				%>
				<% if(i ==1) {
					String strPgCnt = (String)request.getAttribute("pageNumber");
					String strMinLimit = (String)request.getAttribute("minLimit");
					if(uF.parseToInt(strPgCnt) > 1) {
						 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
						 strMinLimit = (uF.parseToInt(strMinLimit)-50) + "";
					}
					if(strMinLimit == null) {
						strMinLimit = "0";
					}
					if(strPgCnt == null) {
						strPgCnt = "1";
					}
				%>
					<span style="color: lightgray;">
					<% if(uF.parseToInt((String)request.getAttribute("pageNumber")) > 1) { %>
						<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');">
						<%="< Prev" %></a>
					<% } else { %>
						<b><%="< Prev" %></b>
					<% } %>
					</span>
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(((String)request.getAttribute("pageNumber") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("pageNumber")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
					
					<% if((uF.parseToInt((String)request.getAttribute("pageNumber"))-3) > 1) { %>
						<b>...</b>
					<% } %>
				
				<% } %>
				
				<% if(i > 1 && i < intproCnt) { %>
				<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("pageNumber"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("pageNumber"))+2)) { %>
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(((String)request.getAttribute("pageNumber") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("pageNumber")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
				<% } %>
				<% } %>
				
				<% if(i == intproCnt && intproCnt > 1) {
					String strPgCnt = (String)request.getAttribute("pageNumber");
					String strMinLimit = (String)request.getAttribute("minLimit");
					 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
					 strMinLimit = (uF.parseToInt(strMinLimit)+50) + "";
					 if(strMinLimit == null) {
						strMinLimit = "0";
					}
					if(strPgCnt == null) {
						strPgCnt = "1";
					}
					%>
					<% if((uF.parseToInt((String)request.getAttribute("pageNumber"))+3) < intproCnt) { %>
						<b>...</b>
					<% } %>
				
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(uF.parseToInt((String)request.getAttribute("pageNumber")) == pageCnt) { %>
					style="color: black;"
					<% } %>
					><%=pageCnt %></a></span>
					<span style="color: lightgray;">
					<% if(uF.parseToInt((String)request.getAttribute("pageNumber")) < pageCnt) { %>
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
