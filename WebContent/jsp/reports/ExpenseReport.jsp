<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ taglib prefix="s" uri="/struts-tags" %>


<script  type="text/javascript" charset="utf-8">
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

</script>

<script>
function submitForm(type) {
	 <%-- var divResult = 'divResult';
	var strBaseUserType = document.getElementById("strBaseUserType").value;
	var strCEO = '<%=IConstants.CEO %>';
	var strHOD = '<%=IConstants.HOD %>';
	var strManager = '<%=IConstants.MANAGER %>';
	var strUserType = '<%=strUserType %>';
	 
	if(strBaseUserType == strCEO || strBaseUserType == strHOD || strUserType != strManager) {
		divResult = 'subDivResult';
	} --%>
	
	var divResult = 'divResult';
	var org = "";
	var f_strWLocation = "";
	var strSelectedEmpId = "";
	if(document.getElementById("f_org")) {
		org = document.getElementById("f_org").value;
	}
	if(document.getElementById("f_strWLocation")) {
		f_strWLocation = document.getElementById("f_strWLocation").value;
	}
	if(document.getElementById("strSelectedEmpId")) {
		strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
	}
	var currUserType = document.getElementById("currUserType").value;
	
	var paycycleDate = getCheckedValue("paycycleDate");
	var paycycle = document.getElementById("paycycle").value;
	var strStartDate = document.getElementById("strStartDate").value;
	var strEndDate = document.getElementById("strEndDate").value;
	var approveStatus = document.getElementById("approveStatus").value;
	var paramValues = "f_org="+org+"&strf_WLocation="+f_strWLocation+"&currUserType="+currUserType;
	//alert('paycycleDate ===>> ' + paycycleDate);
	if(type == '2') {
		paramValues = paramValues+ '&strSelectedEmpId='+strSelectedEmpId+'&paycycleDate='+paycycleDate+'&paycycle='+paycycle
		+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate+'&approveStatus='+approveStatus;
	}
	//alert("paramValues ===>> " + paramValues);
/* 	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>'); */
 
 
	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ExpenseReport.action?'+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result) {
        	$("#"+divResult).html(result);
   		}
	});
	
}

function getCheckedValue(checkId) {
    var radioObj = document.getElementsByName(checkId);
    var radioLength = radioObj.length;
	for(var i = 0; i < radioLength; i++) {
		if(radioObj[i].checked) {
			return radioObj[i].value;
		}
	}
}


</script>

<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF =  new UtilityFunctions();

String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);

boolean flagBulkExpenses = CF.getFeatureManagementStatus(request, uF, IConstants.F_SHOW_BULK_EXPENSES_LINK);

String strUserTypeId = (String)session.getAttribute(IConstants.USERTYPEID);

String strUserType = (String) session.getAttribute(IConstants.USERTYPE);

Map<String, String> hmUserTypeIdMap = (Map<String, String>) request.getAttribute("hmUserTypeIdMap");
if(hmUserTypeIdMap == null) hmUserTypeIdMap = new HashMap<String, String>();

String strApproveStatus = (String) request.getAttribute("approveStatus");
String currUserType = (String) request.getAttribute("currUserType");

if(strUserType!=null && strUserType.equals(IConstants.MANAGER)) {
	if(currUserType != null && currUserType.equals("MYTEAM")) {
		strUserTypeId = hmUserTypeIdMap.get(IConstants.MANAGER);
	} else {
		strUserTypeId = hmUserTypeIdMap.get(currUserType);
	}
}

List<List<String>> alOuter = (List<List<String>>)request.getAttribute("alOuter");
//System.out.println("alOuter"+alOuter.size());
%>
<div id="divResult">
 <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
     <div class="desgn" style="background:#f5f5f5; color:#232323;">
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
				<s:form theme="simple" name="frm_ExpenseReport" id="frm_ExpenseReport" action="ExpenseReport" method="post">
				
				
				<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
				<s:hidden name="currUserType" id="currUserType"/>
	    		<% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER) && !strUserType.equals(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) || (currUserType != null && currUserType.equals(strBaseUserType))) { %>
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
							<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="All Locations" list="wLocationList" key="" onchange="submitForm('2');"/>	
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Employee</p>
							<s:select theme="simple" name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" listValue="employeeName" headerKey="" headerValue="All Employees" list="empNamesList" key=""/>													
						</div>
					</div>
				</div>
				<%} %>
				<div class="row row_without_margin">
				<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
					<i class="fa fa-calendar"></i>
				</div>
					<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
						<%
						String paycycleDate=(String)request.getAttribute("paycycleDate");
						String check1="";
						String check2="";
						if(paycycleDate!=null && paycycleDate.equals("2")) {
							check1="";
							check2="checked=\"checked\"";	
						}else{
							check1="checked=\"checked\"";
							check2="";
						}
						%>
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="radio" name="paycycleDate" id="paycycleDate" value="1" <%=check1 %>/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Paycycle</p>
							<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleListFull" key=""/>
						</div>
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="radio" name="paycycleDate" id="paycycleDate" value="2" <%=check2 %>/>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">From Date</p>
							<s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 100px !important;"></s:textfield>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">To Date</p>
							<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;"></s:textfield>
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Status</p>
							<s:select theme="simple" name="approveStatus" id="approveStatus" cssStyle="width: 110px !important;" list="#{'0':'All','1':'Approved', '2':'Pending','3':'Denied','4':'Canceled'}" />
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
						</div>
					</div>
				</div>
			
			</s:form>						    
		</div>
		</div>
      </div>  
        
			<div class="clr margintop20"></div>	
				<div class="scroll">
					<table class="table table-bordered" id="lt">
						<thead>
								<tr>
									<th class="alignCenter" nowrap>Sr.NO </th>
									<th class="alignCenter" nowrap>Employee Code</th>
									<th class="alignCenter" nowrap>Employee Name</th>
									<th class="alignLeft" nowrap>  Paycycle- From Date</th>
									<th class="alignCenter" nowrap>Paycycle- To Date</th>
									<th class="alignCenter" nowrap>Expense Incurred Date</th>
									<th class="alignCenter" nowrap>Type</th>
									<th class="alignCenter" nowrap>Description</th>
									<th class="alignCenter" nowrap>Amount</th>
									<th class="alignCenter" nowrap>Submitted Date</th>
									<th class="alignCenter" nowrap>Approval Status</th>
									<th class="alignCenter" nowrap>Approval By</th>
									<th class="alignCenter" nowrap>Approval Date</th>
								</tr>
						</thead>
						<tbody>
							<% if(alOuter!=null && alOuter.size()>0){ 
								for(int i=0;i<alOuter.size();i++){
									List<String> alInner=alOuter.get(i);%>
							<tr>
							<%for(int i1=0;i1<alInner.size();i1++){ %>
								<td><%=alInner.get(i1)%></td>
								<%} %>
							</tr>
								
							<%} }%>
						</tbody>
						
					</table>
				</div>
			
			
			</div>
            <!-- /.box-body -->
	</div>		