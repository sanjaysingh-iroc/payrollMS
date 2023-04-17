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
%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript" charset="utf-8">
$(function() {
	$("body").on('click','#closeButton',function() {
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
    });
	
	$("body").on('click','.close',function() {
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
});

function isNumberKey(evt)
{
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

/* function submitForm(type){
	if(type == '1'){
		document.getElementById("strf_WLocation").selectedIndex = "0";
		document.getElementById("strSelectedEmpId").selectedIndex = "0";
	} else if(type == '2'){
		document.getElementById("strSelectedEmpId").selectedIndex = "0";
	}
	document.frmPerkInSalary.submit();
} */

function approveDeny(apStatus,perkId,userType){
	var status = '';
	if(apStatus == '1'){
		status='approve';
	} else if(apStatus == '-1'){
		status='deny';
	}
	if(confirm('Are you sure, do you want to '+status+' this request?')){
		var reason = window.prompt("Please enter your "+status+" reason.");
		if (reason != null) {
			var nonTaxable = document.getElementById("nonTaxable_"+perkId).checked;;
			var action = 'ApplyPerkInSalary.action?type=PERKSALARY&apStatus='+apStatus+'&PID='+perkId+'&mReason='+reason+'&userType='+userType+'&nonTaxable='+nonTaxable;
			//window.location = action;
			$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type:'GET',
				url:action,
				cache:false,
				success:function(result){
					$("#subDivResult").html(result);
				}
			});
		}
	}
}

function applyPerkInSalary() {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Apply for New Perk In Salary');
	$.ajax({
		url : 'ApplyPerkInSalary.action',
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
			$("#paycycle").multiselect().multiselectfilter();
		}
	});
}

function submitForm(type){
	var org = "";
	var strf_WLocation ="";
	var strSelectedEmpId = "";
	var f_financialYear = "";
	if(document.getElementById("f_org")) {
		org = document.getElementById("f_org").value;
	}
	
	if(document.getElementById("strf_WLocation")) {
		strf_WLocation = document.getElementById("strf_WLocation").value;
	}
	
	if(document.getElementById("strSelectedEmpId")) {
		strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
	}
	
	if(document.getElementById("f_financialYear")) {
		f_financialYear = document.getElementById("f_financialYear").value;
	}
	
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strf_WLocation='+strf_WLocation;
	}
	
	if(type == '3') {
		paramValues =  '&strf_WLocation='+strf_WLocation+'&strSelectedEmpId='+strSelectedEmpId+'&f_financialYear='+f_financialYear;
	}
	
	//alert("paramValues ===>> " + paramValues);
	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PerkInSalary.action?f_org='+org+paramValues,
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
		var value = choice.options[i].value;
		if(choice.options[i].selected == true && value != ""){
			
			if (j == 0) {
				exportchoice =  choice.options[i].value ;
				j++;
			} else {
				exportchoice += ","+ choice.options[i].value ;
				j++;
			}
		}else if(choice.options[i].selected == true && value == ""){
			exportchoice = "";
			break;
		}
		
	}
	//alert("exportchoice==>"+exportchoice);
	return exportchoice;
}

</script>

<%
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	String currUserType = (String) request.getAttribute("currUserType");
	String strBaseUserType = (String) session.getAttribute(IConstants.BASEUSERTYPE);
%>
		
	<%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "")%>
	<%session.setAttribute(IConstants.MESSAGE, ""); %>
	<!-- /.box-header -->
	<div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
		<div class="box box-default collapsed-box">
			<div class="box-header with-border">
			    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			    <div class="box-tools pull-right">
			        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<s:form name="frmPerkInSalary" action="PerkInSalary" theme="simple" method="post">
					<s:hidden name="currUserType" />
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<% if (strUserType != null && (strUserType.equals(IConstants.ADMIN) ||strUserType.equals(IConstants.HRMANAGER) || strUserTYpe.equalsIgnoreCase(IConstants.ACCOUNTANT))) { %>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple"  name="strf_WLocation" id="strf_WLocation" listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="All Locations" list="wLocationList" key="" onchange="submitForm('2');"/> 
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Employee</p>
								<s:select theme="simple" name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" listValue="employeeCode" headerKey="" headerValue="All Employees" list="empNamesList" key="" onchange="submitForm('3');"/>
							</div>
							<% } %>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Financial Year</p>
								<s:select theme="simple" name="f_financialYear" id="f_financialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" key="" onchange="submitForm('3');"/>
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
		
		<% if(strUserType != null && strUserType.equals(IConstants.EMPLOYEE)) { %>
			<div class="col-md-12" style="margin: 0px 0px 10px 0px">
				<a href="javascript:void(0)" onclick="applyPerkInSalary();" title="Apply for New Perk"><i class="fa fa-plus-circle" aria-hidden="true"></i> Apply for New Perk In Salary</a>
			</div>
		<% } %>
		
		<table class="table table-bordered" id="lt">
			<thead>
				<tr>
					<th style="text-align: left; width: 80%;">Perk In Salary</th>
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
				<% } %>
			</tbody>
		</table>
	</div>

	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">-</h4>
	            </div>
	            <div class="modal-body" style="height:auto;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>
	