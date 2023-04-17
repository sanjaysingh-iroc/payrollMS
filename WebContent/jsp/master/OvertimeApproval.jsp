<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%-- <link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
	<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script> --%>

<script type="text/javascript">

$(function(){
	$("#f_wLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
});

</script> 

<script type="text/javascript">
jQuery(document).ready(function() {

	jQuery(".content1").hide();
	//toggle the componenet with class msg_body
	jQuery(".heading_dash").click(function() {
		jQuery(this).next(".content1").slideToggle(500);
		$(this).toggleClass("filter_close");
	});
});

function generateSalaryExcel(){
	window.location="ExportExcelReport.action";
}

function loadMore(pageNumber, minLimit) {
	document.frm_DailyOverTimeReport.pageNumber.value = pageNumber;
	document.frm_DailyOverTimeReport.minLimit.value = minLimit;
	document.frm_DailyOverTimeReport.submit();
}

function searchEmp(){
	document.frm_DailyOverTimeReport.pageNumber.value = '';
	document.frm_DailyOverTimeReport.minLimit.value = '';
	document.frm_DailyOverTimeReport.submit();
}


function submitForm(type) {
	var org = "";
	var location = "";
	var department = "";
	var service = "";
	var strSearch = document.getElementById("strSearch").value;
	
	if(document.getElementById("f_org"))
		org = document.getElementById("f_org").value;
	if(document.getElementById("f_wLocation"))
		location = getSelectedValue("f_wLocation");
	if(document.getElementById("f_department"))
		department = getSelectedValue("f_department");
	if(document.getElementById("f_service"))
		service = getSelectedValue("f_service");
	
	var paycycle = document.getElementById("paycycle").value;
	
	var divResult = 'divResult';
	var paramValues = "";
	if(type != "" && type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&paycycle='+paycycle;
	}
	if(strSearch!=''){
		 paramValues = paramValues+'&strSearch='+strSearch;
	}
	var action = 'OvertimeApproval.action?f_org='+org+paramValues;
	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: action,
		success: function(result){
			$("#"+divResult).html(result);
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


function loadMore(proPage, minLimit) {
		
		var org = "";
		var location = "";
		var department = "";
		var service = "";
		var strSearch = document.getElementById("strSearch").value;
		
		if(document.getElementById("f_org"))
			org = document.getElementById("f_org").value;
		if(document.getElementById("f_wLocation"))
			location = getSelectedValue("f_wLocation");
		if(document.getElementById("f_department"))
			department = getSelectedValue("f_department");
		if(document.getElementById("f_service"))
			service = getSelectedValue("f_service");
		
		var paycycle = document.getElementById("paycycle").value;
		
		var divResult = 'divResult';
		var paramValues = "";
		if(type != "" && type == '2') {
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&paycycle='+paycycle;
		}
		if(strSearch!=''){
			 paramValues = paramValues+'&strSearch='+strSearch;
		}
	
		var action = 'OvertimeApproval.action?f_org='+org+paramValues;
		$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: action,
			success: function(result){
				$("#"+divResult).html(result);
	   		}
		});
		
}


</script>


<%
UtilityFunctions uF = new UtilityFunctions();
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);

String  strYear = (String)request.getAttribute("strYear");
String  strMonth = (String)request.getAttribute("strMonth");
String  strTitle = (String)request.getAttribute(IConstants.TITLE);

List<Map<String, String>> alOT = (List<Map<String, String>>)request.getAttribute("alOT"); 
if(alOT == null) alOT = new ArrayList<Map<String,String>>();

String sbData = (String) request.getAttribute("sbData");
String strSearch = (String) request.getAttribute("strSearch");

%>

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
			    <s:form name="frm_DailyOverTimeReport" action="OvertimeApproval" theme="simple" method="post">
			    	<s:hidden name="pageNumber" id="pageNumber" />
				    <s:hidden name="minLimit" id="minLimit" />
		    
		    		<div class="box box-default" style="margin-top: 10px;">  <!-- collapsed-box -->
						<%-- <div class="box-header with-border">
							<h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
								<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							</div>
						</div> --%>
						<!-- /.box-header -->
						<div class="box-body" style="padding: 5px; overflow-y: auto;">  <!-- display: none; -->
							<% if(strUserType != null && !strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && !strBaseUserType.equals(IConstants.HOD)) { %>
								<div class="row row_without_margin">
									<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
										<i class="fa fa-filter"></i>
									</div>
									<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Organization</p>
											<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key="" />
										</div>
										<div
											class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Location</p>
											<s:select theme="simple" name="f_wLocation" id="f_wLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key="" />
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Department</p>
											<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
										</div>
		
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Service</p>
											<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
										</div>
	
									</div>
								</div>
							<%} %>
							
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Paycycle</p>
										<s:select name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="0" headerValue="Select Paycycle" list="paycycleList" key="" onchange="submitForm('2');" />
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
									</div>
								</div>
							</div>
						</div>
					</div>
					
		    
		    		<div class="col-lg-12 col-md-12 col-sm-12 no-padding" style="text-align: center;">
						<input type="text" id="strSearch" class="form-control" name="strSearch" placeholder="Search" value="<%=uF.showData(strSearch, "") %>"/>
						<input type="button" value="Search" class="btn btn-primary" onclick="submitForm('2');">
					</div>	
					
					<%-- <div style="float:left; font-size:12px; line-height:22px; width:100%; margin-left: 350px;margin-bottom: 10px;">
				         <span style="float:left; margin-right:7px;">Search:</span>
				         <div style="border:solid 1px #68AC3B; float:left; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
				           <div style="float:left">
				           	<input type="text" id="strSearch" name="strSearch" style="margin-left: 0px; border:0px solid #ccc; width:282px; box-shadow:0px 0px 0px #ccc" value="<%=uF.showData(strSearch,"") %>"/> 
				         	</div>
				        	 <div style="float:right">
				           	<input type="button" value="Search" class="input_search" onclick="searchEmp();"/>
				           </div>
				     	</div>
				    </div> --%>
	      
				    <script type="text/javascript">
						$( "#strSearch" ).autocomplete({
							source: [ <%=uF.showData(sbData,"") %> ]
						});
					</script>
			
				   	<div style="width:100%;float:left;">
				   		 <ul class="level_list">
				   		 	<%
				   		 		int nAlOT = alOT.size();
				   		 		if(nAlOT > 0){
				   		 			for(int i = 0; i < nAlOT; i++){
				   		 				Map<String, String> hmCalculateOT = alOT.get(i);
				   		 				if(hmCalculateOT == null) hmCalculateOT = new HashMap<String, String>();
				   		 	%>
				   		 				<li>
					   		 				A new Overtime request has been generated for <strong><%=uF.showData(hmCalculateOT.get("EMP_NAME"),"") %></strong>, 
					   		 				on <strong><%=uF.showData(hmCalculateOT.get("OVERTIME_DATE"),"") %></strong>, 
					   		 				for <strong><%=uF.showData(hmCalculateOT.get("OVERTIME_VIEW"),"") %></strong>Hrs.
					   		 				<span id="myDiv_<%=i%>">
									    		<%-- <img onclick="(confirm('Are you sure, you want to approve this overtime?')?getContent('myDiv_<%=i%>', 'UpdateOvertime.action?type=otMinute&approval=1&emp_id=<%=uF.parseToInt(hmCalculateOT.get("EMP_ID"))%>&otDate=<%=hmCalculateOT.get("OVERTIME_DATE")%>&otHours=<%=hmCalculateOT.get("OVERTIME")%>&count=<%=i%>'):'')" width="17px" src="images1/icons/icons/approve_icon.png"/> --%>
									    		<i class="fa fa-check-circle checknew" aria-hidden="true" title="Approve" onclick="(confirm('Are you sure, you want to approve this overtime?')?getContent('myDiv_<%=i%>', 'UpdateOvertime.action?type=otMinute&approval=1&emp_id=<%=uF.parseToInt(hmCalculateOT.get("EMP_ID"))%>&otDate=<%=hmCalculateOT.get("OVERTIME_DATE")%>&otHours=<%=hmCalculateOT.get("OVERTIME")%>&count=<%=i%>'):'')"></i>
									    		<%-- <img onclick="(confirm('Are you sure, you want to deny this overtime?')?getContent('myDiv_<%=i%>', 'UpdateOvertime.action?type=otMinute&approval=-1&emp_id=<%=uF.parseToInt(hmCalculateOT.get("EMP_ID"))%>&otDate=<%=hmCalculateOT.get("OVERTIME_DATE")%>&otHours=<%=hmCalculateOT.get("OVERTIME")%>&count=<%=i%>'):'')" width="16px" src="images1/icons/icons/close_button_icon.png"/> --%>
									    		<i class="fa fa-times-circle cross" aria-hidden="true" title="Deny" onclick="(confirm('Are you sure, you want to deny this overtime?')?getContent('myDiv_<%=i%>', 'UpdateOvertime.action?type=otMinute&approval=-1&emp_id=<%=uF.parseToInt(hmCalculateOT.get("EMP_ID"))%>&otDate=<%=hmCalculateOT.get("OVERTIME_DATE")%>&otHours=<%=hmCalculateOT.get("OVERTIME")%>&count=<%=i%>'):'')"></i>
								    		</span>
					   		 				
				   		 				</li>			
				   		 	<%
				   		 			}
				   		 		} else {
				   		 	%>	
				   		 		<li><div class="filter"><div class="msg nodata"><span>No Data Found.</span></div></div></li>	
				   		 	<%	} %>
				   		 </ul>
					</div>
		
					<div style="text-align: center; float: left; width: 100%;">
						<% 
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
		</section>
</section>