<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<script type="text/javascript" charset="utf-8">

	function submitForm(type){
		
		var org = document.getElementById("f_org").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var paramValues = "";
		if(type == '2') {
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level;
		}if(type=='3'){
			paramValues = '&location='+location+'&strDepartment='+department+'&strLevel='+level+'&strSbu='+service+'&exceldownload=true';
		}	
		//alert("location ===>> " + location);
		//alert("paramValues ===>> " + paramValues);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'LeaveRegularise.action?f_org='+org+paramValues,
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
	}


	
	
	function downloadForm() {
		var org = document.getElementById("f_org").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var paramValues = "";
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&exceldownload=true';
		window.location='LeaveRegularise.action?f_org='+org+paramValues;
		//alert("service ===>> " + service);
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

	
	function updateLeaveData() {
		if(confirm('Are you sure, you want to update leave balance of selected employee?')){
			var data = $("#frm_LeaveRegularise").serialize();
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'LeaveRegularise.action?loginSubmit=UPDATE',
				data: data,
				success: function(result){
		        	$("#divResult").html(result); 
		   		}
			}); 
			//$.post('ApproveAttendance.action', $('#frm_ApproveAttendance').serialize());
			/* document.getElementById("formType").value = "approve";
			document.frm_ApproveAttendance.submit(); */
			//showLoading(); 
		}
	}

	function checkAllCandidateCheckedUnchecked() {
		var allCandi = document.getElementById("allEmployee");
		var candidate = document.getElementsByName('employee');
		var can1 = document.getElementById("employee").value;
		var cnt = 0;
		var chkCnt = 0;
		for ( var i = 0; i < candidate.length; i++) {
			cnt++;
			if (candidate[i].checked) {
				chkCnt++;
			}
		}
		if (parseFloat(chkCnt) > 0) {
			document.getElementById("unblockedSpan").style.display = 'none';
			document.getElementById("blockedSpan").style.display = 'inline';
		} else {
			document.getElementById("unblockedSpan").style.display = 'inline';
			document.getElementById("blockedSpan").style.display = 'none';
		}

		if (cnt == chkCnt) {
			allCandi.checked = true;
		} else {
			allCandi.checked = false;
		}
	}

	function checkUncheckallEmployee() {
		var allCandi = document.getElementById("allEmployee");
		var candidate = document.getElementsByName('employee');
		//alert("allLivePr.checked ==>> " + allLivePr.checked);
		if (allCandi.checked == true) {
			for ( var i = 0; i < candidate.length; i++) {
				candidate[i].checked = true;
			}
			document.getElementById("unblockedSpan").style.display = 'none';
			document.getElementById("blockedSpan").style.display = 'block';
		} else {
			for ( var i = 0; i < candidate.length; i++) {
				candidate[i].checked = false;
			}
			document.getElementById("unblockedSpan").style.display = 'block';
			document.getElementById("blockedSpan").style.display = 'none';
		}
	}
	
	function getCheckedValue() {
		//var choice = document.getElementById(checkedId);
		var candidate = document.getElementsByName('employee');

		var exportchoice = "";
		for ( var i = 0, j = 0; i < candidate.length; i++) {
			if (candidate[i].checked) {
				if (j == 0) {
					exportchoice = candidate[i].value;
					j++;
				} else {
					exportchoice += "," + candidate[i].value;
					j++;
				}
			}
		}
		//alert("exportchoice ====>> " + exportchoice);
		return exportchoice;
	}
	
	function importLeaveregularise(){ 
     	var f_org=document.getElementById("f_org").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var strEffectiveDate = document.getElementById("idEffectiveDate").value;
		
		var paramValues = "";
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&strEffectiveDate='+strEffectiveDate;
     	var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Import Leave Regularise');
		 $.ajax({
			url : "ImportLeaveRegularisation.action?f_org="+f_org+paramValues,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
	List<List<String>> reportList = (List<List<String>>) request.getAttribute("reportList");
	if(reportList == null) reportList = new ArrayList<List<String>>();
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
%>
<!-- Custom form for adding new records -->

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>     
</jsp:include> --%>



		<div class="box-body" style="padding: 5px; overflow-y: auto;min-height: 600px;">
			<%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "")%>
			<%session.setAttribute(IConstants.MESSAGE, ""); %>
			
	<%-- <s:form theme="simple" name="frm_ImportLeaveRegularise" id="frm_ImportLeaveRegularise" action="ImportLeaveRegularisation" enctype="multipart/form-data" method="POST">
					<s:hidden name="f_org"></s:hidden>
					<s:hidden name="location"></s:hidden>
					<s:hidden name="strDepartment"></s:hidden>
					<s:hidden name="strLevel"></s:hidden>
					<s:hidden name="strSbu"></s:hidden>
					
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
			<s:form name="frm_LeaveRegularise" id="frm_LeaveRegularise" action="LeaveRegularise" theme="simple">
				<div class="box box-primary collapsed-box" style="margin-top: 20px; border-top-color: #EEEEEE;">
	                <div class="box-header with-border">
	                    <p class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></p>
	                    <div class="box-tools pull-right">
	                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
	                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                    </div>
	                </div>
	                <!-- /.box-header -->
	                <div class="box-body" style="padding: 5px; overflow-y: auto;display:none;">
	                	<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">SBU</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
								</div>
								<a href="javascript:void(0)" title="Export to Excel" class="excel pull-right" onclick="downloadForm();"></a>
								<!-- <a onclick="downloadForm();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');" />
								</div>
							</div>
						</div><br>
	                </div>
	                <!-- /.box-body -->
	            </div>
	
				<div class="col-md-12">
					Effective From: <s:textfield id="idEffectiveDate" name="strEffectiveDate" cssStyle="width: 100px !important;" />&nbsp;&nbsp;&nbsp;&nbsp;
					<span id="unblockedSpan"><input type="button" name="blocked" class="btn btn-primary disabled" value="UPDATE" /> </span>
					<span id="blockedSpan" style="display: none;"> <input type="button" value="UPDATE" name="loginSubmit" class="btn btn-primary" onclick="updateLeaveData()" /></span> <!-- onclick="return confirm('Are you sure, you want to update leave balance of selected employee?')" -->
				</div>
				<div class="clr" style="padding-top: 20px;"></div>
				<div style="text-align: right;"> 
					<a href="javascript:void(0)" onclick="importLeaveregularise();">Import Leave Regularise</a>
				</div>
				<table id="lt" class="table table-bordered" style="width: 100%">
					<thead>
					    <tr>	
					    	<th style="text-align: left;">
								<% if (reportList != null && reportList.size() > 0) { %>
									<input type="checkbox" name="allEmployee" id="allEmployee" onclick="checkUncheckallEmployee();" />
								<% } %>
							</th>
					    	<th style="text-align: left;">Employee Code</th>
					    	<th style="text-align: left;">Employee Name</th>
					      	<th style="text-align: left;">Leave Type</th>
					    	<th style="text-align: left;">Leave Balance</th>
					    	
					    </tr>
				  	</thead>
				
					<tbody>  
					     
					
					    <%
				    	for(int i=0; reportList!=null && i<reportList.size(); i++){	
				    		List<String> cinnerlist = (List<String>) reportList.get(i);
				     %>
				     
					    <tr>
					    	<td><input type="checkbox" value="<%=cinnerlist.get(0)%>_<%=cinnerlist.get(4)%>" name="employee" id="employee" onclick="checkAllCandidateCheckedUnchecked();"/></td>
					    	<td><%=cinnerlist.get(1)%></td>
					    	<td><%=cinnerlist.get(2)%></td>
					    	<td><%=cinnerlist.get(3)%></td>
					    	<td style="text-align: right;">
					    		<input style="width:100px !important;text-align: right" type="text" id="idClosingBalance" name="strBalance_<%=cinnerlist.get(0)%>_<%=cinnerlist.get(4)%>" value="<%=cinnerlist.get(5)%>"/>
					    	</td>
					    </tr>
					  
				    <%} %>
				   	</tbody>
				</table>
			</s:form>
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
	
<script>
	$(function(){
		$("#lt").DataTable({
		dom: 'lBfrtip',
	    buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
	    ]
		});

		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter();
		
		$("#idEffectiveDate").datepicker({format : 'dd/mm/yyyy'});
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
 	
</script>
