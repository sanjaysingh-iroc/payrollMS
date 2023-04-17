<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%@taglib uri="/struts-tags" prefix="s"%>

<div id="divResult">

<%-- <%	String btnSubmit = (String)request.getAttribute("btnSubmit");
	System.out.println("btnSubmit ===>> " + btnSubmit);
	if(btnSubmit == null || btnSubmit.equalsIgnoreCase("null") || btnSubmit.equals("")) {
%> --%>
	<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"> </script> --%>
<%-- <% } %> --%>

<script>
$(function() {
	$('#lt1').DataTable({
		dom: 'lBfrtip',
		buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
		]
	});
	
	$( "#strStartDate" ).datepicker({format: 'dd/mm/yyyy'});
    $( "#strEndDate" ).datepicker({format: 'dd/mm/yyyy'});
    
    var value = document.getElementById("selectOne").value;
    checkSelectType(value);
});
    
    function checkSelectType(value) {
    	
    	//fromToDIV financialYearDIV monthDIV paycycleDIV
    	if(value == '1') {
    		document.getElementById("fromToDIV").style.display = 'block';
    		document.getElementById("financialYearDIV").style.display = 'none';
    		document.getElementById("monthDIV").style.display = 'none';
    		document.getElementById("paycycleDIV").style.display = 'none';
    	} else if(value == '2') {
    		document.getElementById("fromToDIV").style.display = 'none';
    		document.getElementById("financialYearDIV").style.display = 'block';
    		document.getElementById("monthDIV").style.display = 'none';
    		document.getElementById("paycycleDIV").style.display = 'none';
    	} else if(value == '3') {
    		document.getElementById("fromToDIV").style.display = 'none';
    		document.getElementById("financialYearDIV").style.display = 'none';
    		document.getElementById("monthDIV").style.display = 'block';
    		document.getElementById("paycycleDIV").style.display = 'none';
    	} else if(value == '4') {
    		document.getElementById("fromToDIV").style.display = 'none';
    		document.getElementById("financialYearDIV").style.display = 'none';
    		document.getElementById("monthDIV").style.display = 'none';
    		document.getElementById("paycycleDIV").style.display = 'block';
    	}
    }
    
    
   	 function submitForm(type) {
   		 //alert("hii");
   		 var parameters = "";
   		 if(type == '1') {
   			var f_org = document.getElementById("f_org").value;
   			var strType = document.getElementById("strType").value;
   			parameters = "?f_org="+f_org+"&strType="+strType;
   		 } else {
   		 	document.getElementById("strType").value = type;
   		 	parameters = '?strType='+type;
   		 }
		/* var data = "";
		if(type == '1') {
			var f_org = document.getElementById("f_org").value;
			var strProType = '';
			if(document.getElementById("strProType")) {
				strProType = document.getElementById("strProType").value;
			}
			var strType = document.getElementById("strType").value;
			data = 'f_org='+f_org+'&strProType='+strProType+'&strType='+strType;
		} else if(type == '2') {
			data = $("#frmProfitabilityByIndustry").serialize();
		} else {
			data = 'strType='+type;
		}
		//alert(data);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ProfitabilityByIndustry.action?btnSubmit=Submit',
			data: data,
			success: function(result){
	        	$("#divResult").html(result);
	        	$("#f_strWLocation").multiselect().multiselectfilter();
	        	$("#f_department").multiselect().multiselectfilter();
	        	$("#f_service").multiselect().multiselectfilter();
	        	$("#f_level").multiselect().multiselectfilter();
	        	$("#f_project_service").multiselect().multiselectfilter();
	        	$("#f_client").multiselect().multiselectfilter();
	   		}
		}); */
		
		window.location = 'ProfitabilityByIndustry.action'+parameters;
    }
   
   	 
   	 
   	 
</script>


<%-- <link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script> --%>

<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_project_service").multiselect().multiselectfilter();
	$("#f_client").multiselect().multiselectfilter();
}); 

</script>





	<% 
	UtilityFunctions uF = new UtilityFunctions();
	String strType = (String) request.getAttribute("strType");
	String title = "Industry";

		if(strType != null && strType.equals("I")) {
			title = "Industry";
		} else if(strType != null && strType.equals("S")) {
			title = "Service Name";
		} else if(strType != null && strType.equals("WL")) {
			title = "Work Location Name";
		} else if(strType != null && strType.equals("O")) {
			title = "Organization Name";
		} else if(strType != null && strType.equals("D")) {
			title = "Department Name";
		} else if(strType != null && strType.equals("C")) {
			title = "Client Name";
		} else if(strType != null && strType.equals("P")) {
			title = "Project Name";
		}
		%>
  

	<section class="content">
		<!-- title row -->
		<div class="row">
			<div class="col-lg-12 col-md-12 col-sm-12">
			<div class="nav-tabs-custom">
				<ul class="nav nav-tabs">
				<!-- Created By Dattatray Date:05-10-21 Note:Industry commited -->
					<%-- <li class="<%=((strType == null || strType.equalsIgnoreCase("null") || strType.equalsIgnoreCase("I")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="submitForm('I')" data-toggle="tab">By Industry</a></li> --%>
					<li class="<%=((strType != null && strType.equalsIgnoreCase("S")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="submitForm('S')" data-toggle="tab">By Service</a></li>
					<li class="<%=((strType != null && strType.equalsIgnoreCase("WL")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="submitForm('WL')" data-toggle="tab">By Work Location</a></li>
					<li class="<%=((strType != null && strType.equalsIgnoreCase("O")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="submitForm('O')" data-toggle="tab">By Organization</a></li>
					<li class="<%=((strType != null && strType.equalsIgnoreCase("D")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="submitForm('D')" data-toggle="tab">By Department</a></li>
					<li class="<%=((strType != null && strType.equalsIgnoreCase("C")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="submitForm('C')" data-toggle="tab">By Client</a></li>
					<li class="<%=((strType != null && strType.equalsIgnoreCase("P")) ? "active" : "") %>"><a href="javascript:void(0)" onclick="submitForm('P')" data-toggle="tab">By Project</a></li>
				</ul>

			<div class="tab-content box-body">
				<s:form name="frmProfitabilityByIndustry" id="frmProfitabilityByIndustry" action="ProfitabilityByIndustry" theme="simple">
					<s:hidden name="strType" id="strType" />
					<div class="box box-default collapsed-box">
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
									<%
										boolean poFlag = (Boolean) request.getAttribute("poFlag");
										if(poFlag) {
									%>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Project Type</p> 
											<s:select theme="simple" name="strProType" id="strProType" headerKey="1" headerValue="All Projects"
								                list="#{'2':'My Projects'}" onchange="document.frmProfitabilityByIndustry.submit();"/>
										</div>
									<%} %>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Organisation</p> 
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" 
											onchange="submitForm('1');" list="organisationList" />
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">SBU</p>
										<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Level</p>
										<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Service</p>
										<s:select name="f_project_service" id="f_project_service" list="projectServiceList" listKey="serviceId" listValue="serviceName" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Client</p>
										<s:select name="f_client" id="f_client" listKey="clientId" listValue="clientName" list="clientList" key="" multiple="true" />
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
										<s:select theme="simple" name="selectOne" id="selectOne" headerKey="" headerValue="Select Period" list="#{'1':'From-To', '2':'Financial Year', '3':'Month', '4':'Paycycle'}" onchange="checkSelectType(this.value);"/>
									</div>
									
									<div id="fromToDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display:none;">
										<p style="padding-left: 5px;">&nbsp;</p>
										<% System.out.println("strStartDate ===>> " + (String)request.getAttribute("strStartDate")); %>
										<input type="text" name="strStartDate" id="strStartDate" placeholder="From Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strStartDate"), "") %>"/>
										<input type="text" name="strEndDate" id="strEndDate" placeholder="To Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strEndDate"), "") %>"/>
						      		</div>
						      		
						      		<div id="financialYearDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display:none;">
										<p style="padding-left: 5px;">Financial Year</p>
										<s:select label="Select PayCycle" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName"
											headerValue="Select Financial Year" list="financialYearList" />
						      		</div>
						      		
						      		<div id="monthDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display:none;">
										<p style="padding-left: 5px;">Financial Year &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Month</p>
										<s:select label="Select PayCycle" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName"
											headerValue="Select Financial Year" list="financialYearList" /> 
										<s:select name="strMonth" id="strMonth" cssStyle="margin-left: 7px; width: 100px !important;" listKey="monthId" listValue="monthName" list="monthList" />	
						      		</div>
						      		
						      		<div id="paycycleDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display:none;">
										<p style="padding-left: 5px;">Paycycle</p>
										<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName"
											headerValue="Select Paycycle" list="paycycleList" />
						      		</div>
				      				
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<!-- <input type="button" name="btnSubmit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/> -->
										<s:submit value="Submit" cssClass="btn btn-primary" cssStyle="margin: 0px;" />
									</div>
									
								</div>
							</div>
						</div>
					</div>
				</s:form>
				
			

			<div class="col-lg-5 col-md-5 col-sm-12">
				        
				<display:table name="alOuter" cellspacing="1" class="table table-bordered" id="lt1">
					<display:column style="width: 25%;" title="<%=title %>"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
					<display:column nowrap="nowrap" valign="top" title="Billable Amount" styleClass="alignRight padRight20" ><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
					<display:column nowrap="nowrap" valign="top" title="Actual Cost" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
					<display:column nowrap="nowrap" valign="top" title="Profit Amount" styleClass="alignRight padRight20" ><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
					<display:column nowrap="nowrap" valign="top" title="Profit %" styleClass="alignRight padRight20" ><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
				</display:table>
			
			</div>
	<div class="col-lg-7 col-md-7 col-sm-12">
				<jsp:include page="/jsp/task/ProjectProfitabilityChart.jsp"></jsp:include>
			</div>
		
		</div>

	</div>

</div>
</div>
</section>

</div>