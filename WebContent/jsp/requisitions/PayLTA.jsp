<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	List<Map<String, String>> reportList = (List<Map<String, String>>) request.getAttribute("reportList");
	if (reportList == null) reportList = new ArrayList<Map<String, String>>();
%>
<script type="text/javascript">
	$(function () {
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
		
		$("#f_service").multiselect().multiselectfilter();
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
		var ltaId = document.getElementsByName('ltaId');
		var cnt = 0;
		var chkCnt = 0;
		for(var i=0;i<ltaId.length;i++) {
			cnt++;
			 if(ltaId[i].checked) {
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
	
	function checkPay(){
		var payCount = document.getElementById("payCount").value;		
		if(parseInt(payCount) > 0 && document.getElementById("unApproveSpan")) {
			document.getElementById("unApproveSpan").style.display = 'none';
			document.getElementById("approveSpan").style.display = 'inline';
		} else {
			if(document.getElementById("unApproveSpan")) {
				document.getElementById("unApproveSpan").style.display = 'inline';
				document.getElementById("approveSpan").style.display = 'none';
				document.getElementById("payAll").checked = false;
			}
		}
	}
	
	function donwloadBankStatement(orgId,salaryHead,strStartDate,strEndDate) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Bank Orders');
		$("#modalInfo").show();

		$.ajax({
			url : "ViewBankStatements.action?type=lta&orgId="+orgId+"&salaryHead="+salaryHead+"&strStartDate="+strStartDate+"&strEndDate="+strEndDate,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function submitForm(type){
		var org = document.getElementById("f_org").value;
		var salaryHead = document.getElementById("salaryHead").value;
		var strStartDate = document.getElementById("strStartDate").value;
		var strEndDate = document.getElementById("strEndDate").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var paramValues = "";
		if(type == '2') {
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
			+'&salaryHead='+salaryHead+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate;
		}
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PayCTCVariable.action?f_org='+org+paramValues,
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
	
	function payLTA(){		
		if(confirm('Are you sure you wish to pay for selected employees?')){
			var form_data = $("form[name='frmPayLTA']").serialize();
	    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    	$.ajax({
				type : 'POST',
				url : "PayCTCVariable.action?strApprove=PAY",
				data: form_data,
				success : function(res) {
					$("#divResult").html(res);
				},
				error : function(res) {
					$.ajax({
						url: 'PayCTCVariable.action?strApprove=',
						cache: true,
						success: function(result){
							$("#divResult").html(result);
				   		}
					});
				}
				
			});
		}    	
	}
	
</script>

<%
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	List<Map<String,String>> alReport = (List<Map<String,String>>)request.getAttribute("alReport");
	if(alReport==null) alReport = new ArrayList<Map<String,String>>();
%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Pay CTC Variable" name="title"/>
</jsp:include> --%>
		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
				<s:form name="frmPayLTA" id="frmPayLTA" action="PayCTCVariable" theme="simple">
					<div class="box box-default collapsed-box">
		                <div class="box-header with-border">
		                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
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
										<p style="padding-left: 5px;">Organization</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Salary Head</p>
										<s:select theme="simple" name="salaryHead" id="salaryHead" listKey="salaryHeadId" listValue="salaryHeadName" headerKey="0" headerValue="Select Salary Head" list="salaryHeadList" key="" required="true" onchange="submitForm('2');"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Service</p>
										<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Level</p>
										<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName"  list="levelList" key="" multiple="true" />
									</div>
								</div>
							</div>
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">From Date</p>
										<s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 100px !important;" readonly="true"></s:textfield>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">To Date</p>
										<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;" readonly="true"></s:textfield>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
									</div>
								</div>
							</div>
		                </div>
		                <!-- /.box-body -->
		            </div>
					
					<%
						int nPayCount = 0;
						if(alReport!=null && alReport.size() > 0 && uF.parseToInt((String)request.getAttribute("salaryHead")) > 0) { %>
						<div class="row row_without_margin paddingtopbottom10">
							<div class="col-lg-10">
								Choose Bank to pay from:&nbsp;&nbsp;<s:select theme="simple" name="bankAccount" listKey="bankId" listValue="bankName" list="bankList" key=""/>
								&nbsp;Choose Bank Account: <s:select theme="simple" name="bankAccountType" id="bankAccountType" list="#{'1':'Primary','2':'Secondary'}"/>
								<span id="unApproveSpan" style="display: none;">
									&nbsp;<input type="button" name="strApprove" class="btn btn-info" value="PAY"/>
								</span>
								<span id="approveSpan">
									&nbsp;<input type="button" name="strApprove" class="btn btn-primary" value="PAY" onclick="payLTA();"/> 
								</span>
							</div>
							<div class="col-lg-2">
								<span class="pull-right">Bank Orders: <a href="javascript:void(0)" onclick="donwloadBankStatement('<%=(String)request.getAttribute("f_org")%>','<%=(String)request.getAttribute("salaryHead")%>','<%=(String)request.getAttribute("strStartDate")%>','<%=(String)request.getAttribute("strEndDate")%>')"><i class="fa fa-files-o" aria-hidden="true"></i></a></span>
							</div>
						</div>
					<%} else { %>
						<span id="unApproveSpan"></span>
						<span id="approveSpan"></span>
					<% } %>
					<div class="clr margintop20">
						<table class="table table-bordered table-striped" id="lt">
							<thead>
								<tr>
									<th class="alignCenter" nowrap="nowrap">Employee Code</th>
									<th nowrap="nowrap">Employee Name</th>
									<th nowrap="nowrap">Salary Head</th>
									<th class="alignCenter" nowrap="nowrap">Pay<br/><input type="checkbox" name="payAll" id="payAll" onclick="selectall(this,'ltaId')" checked="checked"/></th>
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
									<td nowrap="nowrap"><%=innerList.get(4)%></td>
									<td style="text-align: center;">
									<%if(uF.parseToBoolean(innerList.get(5))){ %>
										Paid
									<%} else {
										nPayCount++;
									%>
										<input type="checkbox" name="ltaId" value="<%=innerList.get(0)%>" onclick="checkAll();" checked="checked"/>
									<%} %>
									</td>
									<td style="text-align: center;" nowrap="nowrap"><%=innerList.get(6)%></td>
									<td style="text-align: right;" nowrap="nowrap"><%=innerList.get(7)%></td>
									<td><%=innerList.get(8)%></td>
									<td><%=innerList.get(9)%></td>
								</tr>
								<% } %>
							</tbody>
						</table>
					</div>
				
				<input type="hidden" name="payCount" id="payCount" value="<%=nPayCount %>"/> 
			</s:form>
		</div>
		<!-- /.box-body -->


<script type="text/javascript">
	checkPay();
</script>    

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
	