<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%
    UtilityFunctions uF = new UtilityFunctions();
    List<Map<String,String>> alReport = (List<Map<String,String>>)request.getAttribute("alReport");
    if(alReport==null) alReport = new ArrayList<Map<String,String>>();
    
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
    if (CF == null) 
    	return; 
    %>

<script type="text/javascript">
    $(function (){
    	
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
    	
    });    
    
    function isNumberKey(evt){
        var charCode = (evt.which) ? evt.which : event.keyCode;
        if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){   
          return false;
        }
        return true;
    }
    
    function checkAmt(strEmpId,strEmpName,actualAmount,paycycle){
    	var amount=document.getElementById("strGratuity_"+strEmpId).value;
    	if(parseFloat(amount)>0){
    		if(confirm('Are you sure you wish to pay '+strEmpName+'\'s gratuity amount ?')){
    			var action = 'SetGratuity.action?strEmpId='+strEmpId+'&strActualAmount='+actualAmount+'&strAmount='+amount+'&paycycle='+paycycle;
    			getContent('myDiv'+strEmpId, action);	
    		}
    	}else{
    		alert("Please enter the valid amount.");
    	}
    }
    
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
    	var empId = document.getElementsByName('empId');
    	var cnt = 0;
    	var chkCnt = 0;
    	for(var i=0;i<empId.length;i++) {
    		cnt++;
    		 if(empId[i].checked) {
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
    	if(parseInt(payCount) > 0) {
    		document.getElementById("unApproveSpan").style.display = 'none';
    		document.getElementById("approveSpan").style.display = 'inline';
    	} else {
    		document.getElementById("unApproveSpan").style.display = 'inline';
    		document.getElementById("approveSpan").style.display = 'none';
    		document.getElementById("payAll").checked = false;
    	}
    }
    
    function donwloadBankStatement(orgId) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$(".modal-title").html('Bank Orders');
    	$.ajax({
    		url : "ViewBankStatements.action?type=gratuity&orgId="+orgId,
    		cache : false,
    		success : function(data) {
    		$(dialogEdit).html(data);
    		}
    	});
    }	
    
    function submitForm(type){
		var org = document.getElementById("f_org").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var paramValues = "";
		if(type == '2') {
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level;
		}
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'EmpGratuityReport.action?f_org='+org+paramValues,
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
	
	
	function payGratuity(){
		if(confirm('Are you sure, you wish to pay for selected employees?')) {
			var data = $("#frmEmpGratuity").serialize();
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'EmpGratuityReport.action?strApprove=PAY',
				data: data,
				success: function(result){
		        	$("#divResult").html(result); 
		   		}
			});
		}
	}
	
</script>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 400px;">
		<s:form name="frmEmpGratuity" id="frmEmpGratuity" action="EmpGratuityReport" theme="simple">
			<div class="box box-default collapsed-box">
				<div class="box-header with-border">
					<h4 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h4>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<div class="box-body" style="padding: 15px; overflow-y: auto;">
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
								<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList"/>
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
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');" />
							</div>
						</div>
					</div>
				</div>
			</div>
		<!-- Place holder where add and delete buttons will be generated -->
			<div class="clr margintop20"></div>
			<div class="add_delete_toolbar"></div>
			<div class="row row_without_margin paddingtopbottom10">
				<div class="col-lg-10">
					Choose Bank to pay from:&nbsp;&nbsp;<s:select theme="simple" name="bankAccount" listKey="bankId" listValue="bankName" list="bankList" key=""/>
					&nbsp;Choose Bank Account: <s:select theme="simple" name="bankAccountType" id="bankAccountType" list="#{'1':'Primary','2':'Secondary'}"/>
					<span id="unApproveSpan" style="display: none;">
						&nbsp;<input type="button" name="strApprove" class="btn btn-info" value="PAY"/>
					</span>
					<span id="approveSpan">
						&nbsp;<input type="button" name="strApprove" class="btn btn-primary" value="PAY" onclick="payGratuity();"/>
						<%-- &nbsp;<s:submit name="strApprove" cssClass="btn btn-primary" value="PAY" onclick="return confirm('Are you sure you wish to pay for selected employees?')"/> --%>
					</span>
				</div>
				<%
				int nPayCount = 0;
				if(alReport!=null && alReport.size() > 0){ %>
					<div class="col-lg-2">
						<span class="pull-right">Bank Orders: <a href="javascript:void(0)" onclick="donwloadBankStatement('<%=(String)request.getAttribute("f_org")%>')"><i class="fa fa-files-o" aria-hidden="true"></i></a></span>
					</div>
				<%} %>
			</div>
			<div class="clr margintop20">
				<table class="table table-bordered table-striped" id="lt">
					<thead>
						<tr>
							<th nowrap="nowrap" class="no-sort">Pay<br/><input type="checkbox" name="payAll" id="payAll" onclick="selectall(this,'empId')" checked="checked"/></th>
							<th style="text-align: left;">Employee Code</th>
							<th style="text-align: left;">Employee Name</th>
							<th style="text-align: left;">Joining Date</th>
							<th style="text-align: left;">Employee Status</th>
							<th style="text-align: left;">Years</th>
							<th style="text-align: left;">Months</th>
							<th style="text-align: left;">Days</th>
							<th style="text-align: left;">Calculated Gratuity <br />Amount</th>
							<th style="text-align: left;" class="no-sort">Paid Gratuity <br />Amount</th>
						</tr>
					</thead>
					<tbody>
					<%  int nAlReport = alReport.size();
					for (int i = 0; i < nAlReport; i++) { 
					 List<String> innerList = (List<String>)alReport.get(i); 
					 nPayCount++;
					 %>
						<tr id ="<%=innerList.get(0) %>">
							<td>
								<%if(uF.parseToBoolean(innerList.get(10))){ %>
									<input type="checkbox" name="empId" value="<%=innerList.get(0)%>" onclick="checkAll();" checked="checked"/>
								<%} %>
							</td>
							<td><%=innerList.get(1) %></td>
							<td><%=innerList.get(2) %></td>
							<td><%=innerList.get(3) %></td>
							<td><%=innerList.get(4) %></td>
							<td><%=innerList.get(5) %></td>
							<td><%=innerList.get(6) %></td>
							<td><%=innerList.get(7) %></td>
							<td align="right"><%=innerList.get(8) %></td>
							<td align="right"><%=innerList.get(9) %></td>
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
	                <h4 class="modal-title"></h4>
	            </div>
	            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>