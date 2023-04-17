<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF = new UtilityFunctions();
	String strUserTYpe = (String) session.getAttribute(IConstants.USERTYPE);
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
%>

<script type="text/javascript" charset="utf-8">
$(function(){
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
	
    $('#lt').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
  	});
    
    $("#strf_WLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_grade").multiselect().multiselectfilter();
}); 


function isNumberKey(evt) {
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}


function approveDeny(apStatus, reimCTCHId, userType){
	var status = '';
	if(apStatus == '1'){
		status='approve';
	} else if(apStatus == '-1'){
		status='deny';
	}
	if(confirm('Are you sure, do you want to '+status+' this request?')){
		var reason = window.prompt("Please enter your "+status+" reason.");
		if (reason != null) {
			$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'AddReimbursementCTC.action?type=REIMBURSEMENTCTC&apStatus='+apStatus+'&RCHID='+reimCTCHId+'&mReason='+reason+'&userType='+userType,
				success: function(result){
		        	$("#subDivResult").html(result); 
		   		},
		   		error: function(result){
		   			$.ajax({
		   				url: 'ReimbursementCTC.action',
		   				cache: true,
		   				success: function(result){
		   					$("#subDivResult").html(result);
		   		   		}
		   			}); 
		   		} 
			});
		}
	}
}


function applyNewReimbursementCTC() {
	var f_financialYear = document.getElementById("f_financialYear").value;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Apply New Reimbursement Part Of CTC');
	$.ajax({
		url : 'AddReimbursementCTC.action?financialYear='+f_financialYear,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function submitForm(type) {
	var org = "";
	var strf_WLocation ="";
	var strSelectedEmpId = "";
	var f_financialYear = "";
	var department = "";
	var service = "";
	var level = "";
	var strGrade = "";
	
	if(document.getElementById("f_org")) {
		org = document.getElementById("f_org").value;
	}
	
	/* if(document.getElementById("strf_WLocation")) {
		strf_WLocation = document.getElementById("strf_WLocation").value;
	} */
	
//====	
	if(document.getElementById("strf_WLocation")) {
		strf_WLocation = getSelectedValue("strf_WLocation");
	}
	
	if(document.getElementById("f_department")) {
		department = getSelectedValue("f_department");
	}
	
	if(document.getElementById("f_service")) {
		service = getSelectedValue("f_service");
	}
	
	if(document.getElementById("f_level")) {
		level = getSelectedValue("f_level");
	}
	
	if(document.getElementById("f_grade")) {
		strGrade = getSelectedValue("f_grade");
	}
//===	
	
	if(document.getElementById("strSelectedEmpId")) {
		strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
	}
	
	if(document.getElementById("f_financialYear")) {
		f_financialYear = document.getElementById("f_financialYear").value;
	}
	
	var paramValues = "";
	if(type == '2') {
		//paramValues = '&strf_WLocation='+strf_WLocation;
		paramValues = '&strLocation='+strf_WLocation+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&strGrade='+strGrade;
	}
//	alert("type==>"+type);
	if(type == '3') {
		//paramValues =  '&strf_WLocation='+strf_WLocation+'&strSelectedEmpId='+strSelectedEmpId+'&f_financialYear='+f_financialYear; 
		paramValues =  '&strLocation='+strf_WLocation+'&strSelectedEmpId='+strSelectedEmpId+'&f_financialYear='+f_financialYear+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&strGrade='+strGrade;
	}
	
	//alert("paramValues ===>> " + paramValues);
	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ReimbursementCTC.action?f_org='+org+paramValues,
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	$("#subDivResult").html(result);
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
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	String currUserType = (String) request.getAttribute("currUserType");
	String strBaseUserType = (String) session.getAttribute(IConstants.BASEUSERTYPE);
%>


<div class="leftbox reportWidth">
	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<%session.setAttribute(IConstants.MESSAGE, ""); %>
	<div class="box box-default collapsed-box">
		<div class="box-header with-border">
			<h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			<div class="box-tools pull-right">
				<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			</div>
		</div>
		<div class="box-body" style="padding: 5px; overflow-y: auto;">
			<s:form name="frmReimbursementCTC" action="ReimbursementCTC" theme="simple">
				<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
				<s:hidden name="currUserType" id="currUserType"/>
	    		<% if(strUserType != null && (strUserType.equals(IConstants.ADMIN) ||strUserType.equals(IConstants.HRMANAGER) ||strUserType.equals(IConstants.OTHER_HR) || strUserTYpe.equalsIgnoreCase(IConstants.ACCOUNTANT))) { %>
				<div class="row row_without_margin">
					<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
						<i class="fa fa-filter"></i>
					</div>
					<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Organization</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>													
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Work Location</p>
							<s:select name="strf_WLocation" id="strf_WLocation" listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="All Locations" list="wLocationList" key="" multiple="true" /><!-- onchange="submitForm('2');" -->	
						</div>
						
				<!-- Ajinkya -->		
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
							<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key=""  onchange="getLevelwiseGrade();" />
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
                            <p style="padding-left: 5px;">Grade</p>
                            <div id="myGrade">
                               	<s:select theme="simple" name="f_grade" id="f_grade" list="gradeList" listKey="gradeId" listValue="gradeCode" key="" multiple="true"  />
                            </div>
                        </div>
              <!-- end -->          
                        
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Employee</p>
							<s:select theme="simple" name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" listValue="employeeCode" headerKey="" headerValue="All Employees" list="empNamesList" key="" onchange="submitForm('3');"/>													
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
							<p style="padding-left: 5px;">Financial Year</p>
							<s:select theme="simple" name="f_financialYear" id = "f_financialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" key="" onchange="submitForm('3');"/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('3');"/>
						</div>
					</div>
				</div>
			</s:form>						    
		</div>
	</div>
	
	<% if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
		<div class="col-md-12" style="margin: 0px 0px 10px;">
			<a href="javascript:void(0)" onclick="applyNewReimbursementCTC();" title="Apply New Reimbursement Part Of CTC"><i class="fa fa-plus-circle" ></i> Apply New Reimbursement Part Of CTC</a>
		</div>
		
	<% } %>

	<table class="table table-bordered" id="lt">
		<thead>
			<tr>
				<th style="text-align: left;">Reimbursement</th>
			</tr>
		</thead>
		<tbody>
			<%
				List<List<String>> cOuterList = (List<List<String>>) request.getAttribute("alReport");
				for (int i = 0; cOuterList != null && i < cOuterList.size(); i++) {
					List<String> cInnerList = (List<String>) cOuterList.get(i);
			%>
				<tr>
					<td><%=cInnerList.get(0)%></td>
				</tr>
			<%
				}
			%>
		</tbody>
	</table>
</div>
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">&nbsp;</h4>
            </div>
            <div class="modal-body" style="height:350px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>