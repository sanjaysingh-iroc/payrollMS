<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
	/* function submitForm() {
		document.frm_PayPerks.exportType.value = '';
		document.frm_PayPerks.submit();
	} */
	
	$(function() {
		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter(); 
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
	
	function selectall(x,strEmpId){
		var  status=x.checked; 
		var  arr= document.getElementsByName(strEmpId);
		for(i=0;i<arr.length;i++){ 
	  		arr[i].checked=status;
	 	}
		if(x.checked == true){
			document.getElementById("unApproveSpan").style.display = 'none';
			document.getElementById("approveSpan").style.display = 'inline';
		} else {
			document.getElementById("unApproveSpan").style.display = 'inline';
			document.getElementById("approveSpan").style.display = 'none';
		}
	}
	
	function checkAll(){
		var payAll = document.getElementById("payAll");		
		var perkId = document.getElementsByName('perkId');
		var cnt = 0;
		var chkCnt = 0;
		for(var i=0;i<perkId.length;i++) {
			cnt++;
			 if(perkId[i].checked) {
				 chkCnt++;
			 }
		 }
		if(parseFloat(chkCnt) > 0) {
			document.getElementById("unApproveSpan").style.display = 'none';
			document.getElementById("approveSpan").style.display = 'inline';
		} else {
			document.getElementById("unApproveSpan").style.display = 'inline';
			document.getElementById("approveSpan").style.display = 'none';
		}
		
		if(cnt == chkCnt) {
			payAll.checked = true;
		} else {
			payAll.checked = false;
		}
	}
	
	
	function donwloadBankStatement(orgId,financialYear,strMonth) {
    	var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Bank Orders');
		$("#modalInfo").show();
		$.ajax({
			url : "ViewBankStatements.action?type=perk&orgId="+orgId+"&financialYear="+financialYear+"&strMonth="+strMonth,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	$(document).ready(function(){
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
	});
	
	
	function submitForm(type){
		document.frm_PayPerks.exportType.value = '';
		var org = document.getElementById("f_org").value;
		var financialYear = document.getElementById("financialYear").value;
		var strMonth = document.getElementById("strMonth").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var paramValues = "";
		if(type == '2') {
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
			+'&financialYear='+financialYear+'&strMonth='+strMonth;
		}
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PaidUnpaidPerks.action?f_org='+org+paramValues,
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
	
	
	function payPerk(){
		if(confirm('Are you sure, you wish to pay for selected employees?')) {
			var data = $("#frm_PayPerks").serialize();
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'PaidUnpaidPerks.action?strApprove=PAY',
				data: data,
				success: function(result){
		        	$("#divResult").html(result); 
		   		}
			});
		}
	}
	
</script>

<%
	String strUserTYpe = (String) session.getAttribute(IConstants.USERTYPE);
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	List<List<String>> alReport = (List<List<String>>)request.getAttribute("alReport");
%>


<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Pay Perks" name="title"/>
</jsp:include> --%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<s:form name="frm_PayPerks" id="frm_PayPerks" action="PaidUnpaidPerks" theme="simple" method="post">
			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
				<div class="box-header with-border">
					<h3 class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
						<s:hidden name="exportType"></s:hidden>
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
									<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key=""/>
								</div>
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
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key=""/>
								</div>
							</div>
						</div><br>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" onchange="submitForm('2');" list="financialYearList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select label="Select Month" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" headerKey="1" onchange="submitForm('2');" list="monthList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');" />
								</div>
							</div>
						</div>
				</div>
				<!-- /.box-body -->
			</div>
	
			<%
				int nPayCount = 0;
				if(alReport!=null && alReport.size() > 0){ %>
					<div class="row row_without_margin paddingtopbottom10">
						<div class="col-lg-10">
							Choose Bank to pay from:&nbsp;&nbsp;<s:select theme="simple" name="bankAccount" listKey="bankId" listValue="bankName" list="bankList" key=""/>
							&nbsp;Choose Bank Account: <s:select theme="simple" name="bankAccountType" id="bankAccountType" list="#{'1':'Primary','2':'Secondary'}"/>
							<span id="unApproveSpan" style="display: none;">
								&nbsp;<input type="button" name="strApprove" class="btn btn-primary disabled" value="PAY"/>
							</span>
							<span id="approveSpan">
								&nbsp;<input type="button" name="strApprove" class="btn btn-primary" value="PAY" onclick="payPerk();"/>
								<%-- &nbsp;<s:submit name="strApprove" cssClass="btn btn-primary" value="PAY" onclick="return confirm('Are you sure you wish to pay for selected employees?')"/> --%>
							</span>
						</div>
						<div class="col-lg-2">
							<span class="pull-right">Bank Orders: <a href="javascript:void(0)" onclick="donwloadBankStatement('<%=(String)request.getAttribute("f_org") %>','<%=(String)request.getAttribute("financialYear") %>','<%=(String)request.getAttribute("strMonth")%>')"><i class="fa fa-files-o"></i></a></span>
						</div>
					</div>
				<%} %>
	
				<div class="clr margintop20">
				<table class="table table-bordered table-striped" id="lt">
					<thead>
						 <tr>
							<th class="alignCenter" nowrap="nowrap">Employee Code</th>
							<th nowrap="nowrap">Employee Name</th>
							<th class="alignCenter" nowrap="nowrap">Pay<br/><input type="checkbox" name="payAll" id="payAll" onclick="selectall(this,'perkId')" checked="checked"/></th>
							<th class="alignCenter" nowrap="nowrap">Applied Date</th>
							<th class="alignRight" nowrap="nowrap">Amount</th>
							<th>Purpose</th>
							<th nowrap="nowrap" class="no-sort">View Attachment</th>
						</tr>
					</thead>
					<tbody>
					<% 
					   for (int i=0; alReport!=null && i<alReport.size(); i++) { 
							List<String> innerList = (List<String>)alReport.get(i); 
					%>
						<tr id="<%=innerList.get(0)%>">
							<td style="text-align: center;" nowrap="nowrap"><%=innerList.get(2)%></td>
							<td nowrap="nowrap"><%=innerList.get(3)%></td>
							<td style="text-align: center;">
								<%if(uF.parseToBoolean(innerList.get(4))){ %>
									Paid
								<%} else {
									nPayCount++;
								%>
									<input type="checkbox" name="perkId" value="<%=innerList.get(0)%>" onclick="checkAll();" checked="checked"/>
								<%} %>
							</td>
							<td style="text-align: center;" nowrap="nowrap"><%=innerList.get(5)%></td>
							<td style="text-align: right;" nowrap="nowrap"><%=innerList.get(6)%></td>
							<td><%=innerList.get(7)%></td>
							<td><%=innerList.get(8)%></td>
						</tr>
					<% } %>
					</tbody>
				</table>
			</div>
			<input type="hidden" name="payCount" id="payCount" value="<%=nPayCount %>"/> 
		</s:form>
	</div>
	<!-- /.box-body -->

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

