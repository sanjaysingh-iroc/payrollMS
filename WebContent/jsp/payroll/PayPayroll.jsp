<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<%
	UtilityFunctions uF = new UtilityFunctions();
	Map hmEmpMap = (Map) request.getAttribute("hmEmpMap");
	Map hmEmpCodeMap = (Map) request.getAttribute("hmEmpCodeMap");
	Map hmPayPayroll = (Map) request.getAttribute("hmPayPayroll");
	Map hmSalaryDetails = (Map) request.getAttribute("hmSalaryDetails");
	Map hmIsApprovedSalary = (Map) request.getAttribute("hmIsApprovedSalary"); 
	if (hmIsApprovedSalary == null)
		hmIsApprovedSalary = new HashMap();
	List alEarnings = (List) request.getAttribute("alEarnings"); 
	List alDeductions = (List) request.getAttribute("alDeductions");
	Map hmLoanPoliciesMap = (Map) request.getAttribute("hmLoanPoliciesMap");
	Map hmEmpLoan = (Map) request.getAttribute("hmEmpLoan");
	List alLoans = (List) request.getAttribute("alLoans");
	if (alLoans == null) {
		alLoans = new ArrayList();
	}
	
	List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
	List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
	
	String pageFrom = (String) request.getAttribute("pageFrom");
	
	String strBaseUserType = (String) session.getAttribute(IConstants.BASEUSERTYPE);
	
	String roundOffCondition = (String)request.getAttribute("roundOffCondition");
	String paymentMode = (String)request.getAttribute("f_paymentMode");
	
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
		
	List<List<String>> alPaycycleList = (List<List<String>>) request.getAttribute("alPaycycleList");
	
	Map hmWorkLocationMap = (Map) request.getAttribute("hmWorkLocationMap");
	Map<String, String> hmEmpWLocationId = (Map<String, String>) request.getAttribute("hmEmpWLocationId");
	
%>


<script type="text/javascript">

	function selectall(x, strEmpId) {
		var status = x.checked;
		var arr = document.getElementsByName(strEmpId);
		for (i = 0; i < arr.length; i++) {
			arr[i].checked = status;
		}
	}

	
	function donwloadBankStatement(orgId,paycycle) {
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Bank Orders');
		 $.ajax({
			url : "ViewBankStatements.action?type=salary&orgId="+orgId+"&strPaycycle="+paycycle,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}

	function generateSalaryExcel() {

		/* var paycycle=document.frm_PayPayroll.paycycle.value;
		var wLocation=document.frm_PayPayroll.wLocation.value;
		var f_department=document.frm_PayPayroll.f_department.value;
		var f_service=document.frm_PayPayroll.f_service.value;
		var level=document.frm_PayPayroll.level.value;
		var url='SalaryPaidExcel.action?paycycle='+paycycle+'&wLocation='+wLocation+'&f_department='+f_department+'&f_service='+f_service+'&level='+level;
		window.location = url; */

		window.location = "ExportExcelReport.action";
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
			}
		}
		
		var paidCount = document.getElementById("paidCount").value;		
		/* if(parseInt(paidCount) > 0) {
			document.getElementById("rcfDisableSpan").style.display = 'none';
			document.getElementById("rcfEnableSpan").style.display = 'inline';
		} else {
			document.getElementById("rcfDisableSpan").style.display = 'inline';
			document.getElementById("rcfEnableSpan").style.display = 'none';
		} */
	}

	
	function submitForm(type, strPaycycle) {
		var org = document.getElementById("f_org").value;
		var strPaycycleDuration = document.getElementById("strPaycycleDuration").value;
		var paycycle = document.getElementById("paycycle").value;
		if(strPaycycle.length>0) {
			paycycle = strPaycycle;
		}
		var f_paymentMode = document.getElementById("f_paymentMode").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var strGrade = getSelectedValue("f_grade");
		var strEmployeType = getSelectedValue("f_employeType");
		var strBankBranch = getSelectedValue("bankBranch");
		
		var paramValues = "";
		if(type == '2') {
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&paycycle='+paycycle
			+'&strPaycycleDuration='+strPaycycleDuration+'&f_paymentMode='+f_paymentMode+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType
			+'&strBankBranch='+strBankBranch;
		}
		if(type == '3') {
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
			+'&strPaycycleDuration='+strPaycycleDuration+'&f_paymentMode='+f_paymentMode+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType
			+'&strBankBranch='+strBankBranch;
		}
		/* if(parseInt(f_paymentMode) == 1) {
			if(document.getElementById("bankAccount")){
				var bankAccount = document.getElementById("bankAccount").value;
				paramValues += '&bankAccount='+bankAccount;
			}
		} */
		
		
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PayPayroll.action?f_org='+org+paramValues,
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
	}
	
	
	function exportXLS() {
		var bankAccount = document.getElementById("bankAccount").value;
	 	var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
		} else {
   			var xhr = $.ajax({
   				url : 'GetBankStatusForNEFT.action?bankAccount='+bankAccount,
   				cache : false,
   				success : function(data) {
					if(data.trim() == 1) {
						//alert("data in ===>> " + data);
						var bankAccount = document.getElementById("bankAccount").value;
						var org = document.getElementById("f_org").value;
						var strPaycycleDuration = document.getElementById("strPaycycleDuration").value;
						var paycycle = document.getElementById("paycycle").value;
						var f_paymentMode = document.getElementById("f_paymentMode").value;
						var location = getSelectedValue("f_strWLocation");
						var department = getSelectedValue("f_department");
						var service = getSelectedValue("f_service");
						var level = getSelectedValue("f_level");
						var strGrade = getSelectedValue("f_grade");
						var strEmployeType = getSelectedValue("f_employeType");
						var paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&paycycle='+paycycle
							+'&strPaycycleDuration='+strPaycycleDuration+'&f_paymentMode='+f_paymentMode+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType
							+'&bankAccount='+bankAccount;
						window.location="PayPayroll.action?download=true&f_org="+org+paramValues;
					} else {
						alert("NEFT Form is not available for this bank.");
					}
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
	
	
	/* function exportXLS() {
		
		var bankAccount = document.getElementById("bankAccount").value;
		var org = document.getElementById("f_org").value;
		var strPaycycleDuration = document.getElementById("strPaycycleDuration").value;
		var paycycle = document.getElementById("paycycle").value;
		var f_paymentMode = document.getElementById("f_paymentMode").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var strGrade = getSelectedValue("f_grade");
		var strEmployeType = getSelectedValue("f_employeType");
		var paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&paycycle='+paycycle
			+'&strPaycycleDuration='+strPaycycleDuration+'&f_paymentMode='+f_paymentMode+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType
			+'&bankAccount='+bankAccount;
		window.location="PayPayroll.action?download=true&f_org="+org+paramValues;
	} */

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
	/* function submitForm(type){
		if(type == '1'){
			document.getElementById("paycycle").selectedIndex = "0";
		} else if(type == '3'){
			document.getElementById("paycycle").selectedIndex = "0";
		}
		document.frm_PayPayroll.submit();
	} */

	function checkBifurcatePayOut(bifurcateId){
		if(bifurcateId.checked == true){
			document.getElementById("payTypeId").style.display = 'none';
			document.getElementById("bifurcateDivId").style.display = 'block';		
		} else {
			document.getElementById("payTypeId").style.display = 'inline';
			document.getElementById("bifurcateDivId").style.display = 'none';
		}
	}

	function addBifuracateSalaryHead(){ 
		var bifurcateSalaryHead = document.getElementById("bifurcateSalaryHead");
		var selItem = bifurcateSalaryHead.selectedIndex;
		if (selItem == -1) {
	        alert("You must first select a salary head.");
	    } else {
			var bifurcateBankAccountType = document.getElementById("bifurcateBankAccountType").value;
			if (bifurcateBankAccountType == '1') {
				 var primarySalaryHead = document.getElementById("primarySalaryHead1");
			     var newOption = bifurcateSalaryHead[selItem].cloneNode(true);
			     
			     bifurcateSalaryHead.removeChild(bifurcateSalaryHead[selItem]);
			     primarySalaryHead.appendChild(newOption);
			     
			     var strVal = "";
			     for (i = 0; i < primarySalaryHead.options.length; i++) {
			    	 if(i==0){
			    		 strVal = primarySalaryHead.options[i].value;	 
			    	 } else {
			    		 strVal = strVal+","+primarySalaryHead.options[i].value;
			    	 }		        
			     }
			     document.getElementById("primarySalaryHead").value=strVal;
			} else if (bifurcateBankAccountType == '2') {
				 var secondarySalaryHead = document.getElementById("secondarySalaryHead1");
			     var newOption = bifurcateSalaryHead[selItem].cloneNode(true);

			     bifurcateSalaryHead.removeChild(bifurcateSalaryHead[selItem]);
			     secondarySalaryHead.appendChild(newOption);
			     
			     var strVal = "";
			     for (i = 0; i < secondarySalaryHead.options.length; i++) {
			    	 if(i==0){
			    		 strVal = secondarySalaryHead.options[i].value;	 
			    	 } else {
			    		 strVal = strVal+","+secondarySalaryHead.options[i].value;
			    	 }		        
			     }
			     document.getElementById("secondarySalaryHead").value=strVal;
			}
	    }
	}

	function removeBifuracateSalaryHead(type){
		if(type == '1'){
			var primarySalaryHead = document.getElementById("primarySalaryHead1");
			var selItem = primarySalaryHead.selectedIndex;
			if (selItem == -1) {
		        alert("You must first select a primary salary head.");
		    } else {
		    	var bifurcateSalaryHead = document.getElementById("bifurcateSalaryHead");
	 			var newOption = primarySalaryHead[selItem].cloneNode(true);
			     
	 			primarySalaryHead.removeChild(primarySalaryHead[selItem]);
	 			bifurcateSalaryHead.appendChild(newOption);
	 			
	 			var strVal = "";
	 		    for (i = 0; i < primarySalaryHead.options.length; i++) {
	 		     	if(i==0){
	 		    		strVal = primarySalaryHead.options[i].value;	 
	 		    	} else {
	 		    		strVal = strVal+","+primarySalaryHead.options[i].value;
	 		    	}		        
	 		    }
	 		    document.getElementById("primarySalaryHead").value=strVal;
		    }
			
		} else if(type == '2'){
			var secondarySalaryHead = document.getElementById("secondarySalaryHead1");
			var selItem = secondarySalaryHead.selectedIndex;
			if (selItem == -1) {
		        alert("You must first select a secondary salary head.");
		    } else {
		    	var bifurcateSalaryHead = document.getElementById("bifurcateSalaryHead");
	 			var newOption = secondarySalaryHead[selItem].cloneNode(true);
			     
	 			secondarySalaryHead.removeChild(secondarySalaryHead[selItem]);
	 			bifurcateSalaryHead.appendChild(newOption);
	 			
	 			var strVal = "";
			    for (i = 0; i < secondarySalaryHead.options.length; i++) {
			    	if(i==0){
			    		strVal = secondarySalaryHead.options[i].value;	 
			    	} else {
			    		strVal = strVal+","+secondarySalaryHead.options[i].value;
			    	}
			    }
			    document.getElementById("secondarySalaryHead").value=strVal;
		    }
		}
	}

	
	function payForm(){
		var bifurcatePayOut = document.getElementById("bifurcatePayOut");
		if(bifurcatePayOut.checked == true){
			var bifurcateSalaryHead = document.getElementById("bifurcateSalaryHead");
			if(bifurcateSalaryHead.options.length <= 0){
				if(confirm('Are you sure you wish to pay for selected employees?')){
					var data = $("#frm_PayPayroll").serialize();
					$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
					$.ajax({
						type : 'POST',
						url: 'PayPayroll.action?strApprove=PAY',
						data: data,
						success: function(result){
				        	$("#divResult").html(result); 
				   		}
					});
					/* return true; */
				} else {
					/* return false; */
				}
			} else {
				alert('Please select the salary head to add in bank account.');
				/* return false; */
			}
		} else {
			if(confirm('Are you sure you wish to pay for selected employees?')){
				var data = $("#frm_PayPayroll").serialize();
				$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					type : 'POST',
					url: 'PayPayroll.action?strApprove=PAY',
					data: data,
					success: function(result){
			        	$("#divResult").html(result); 
			   		}
				});
				/* return true; */
			} else {
				/* return false; */
			}
		}
		return false;
	}
	
	
</script>

<script>
	$(function(){
		$('#lt').DataTable({
			aLengthMenu: [
				[25, 50, 100, 200, -1],
				[25, 50, 100, 200, "All"]
			],
			iDisplayLength: -1,
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
	});
	
	$(function() {
		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter();
		$("#f_grade").multiselect().multiselectfilter();
		$("#f_employeType").multiselect().multiselectfilter();
		$("#bankBranch").multiselect().multiselectfilter();
		
	});    
</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Pay Payroll" name="title"/>
</jsp:include>  --%>

		<div class="col-lg-12 col-md-12 col-sm-12 paddingright0 listMenu1" style="padding-left: 0px;">
			<% 	if(alPaycycleList != null && alPaycycleList.size()>0) {
				for(int i=0; i<alPaycycleList.size(); i++) {
					List<String> innerList = alPaycycleList.get(i);
					String strBgClass = "bg-gray";
					if(uF.parseToInt(innerList.get(2)) == 0 && uF.parseToInt(innerList.get(3)) > 0) {
						strBgClass = "bg-red";
					} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) > 0) {
						strBgClass = "bg-yellow";
					} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) == 0) {
						strBgClass = "bg-green";
					}
			%>
				<div class="col-lg-2 col-xs-6 col-sm-12 paddingright0">
				<!-- small box -->
					<div class="small-box <%=strBgClass %>">
						<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
							<i class="fa fa-money" aria-hidden="true"></i>
						</div>
						<div class="inner" style="padding: 0px 10px; text-align: right;">
							<h3 style="margin: 0px; font-size: 24px;"><%=innerList.get(2) %></h3>
							<div style="margin-top: -5px;">Paid</div>
						</div>
						<div class="inner" style="padding: 0px 10px 2px; text-align: right;">
							<h4 style="margin: 0px;"><%=innerList.get(3) %></h4>
							<div style="margin-top: -5px;">Waiting</div>
						</div>
						<a href="javascript:void(0);" style="font-size: 12px;" onclick="submitForm('2', '<%=innerList.get(0) %>');" class="small-box-footer"><%=innerList.get(1) %><i class="fa fa-arrow-circle-right"></i></a>
					</div>
				</div>
				<% } %>
			<% } %>
		</div>
		
		
		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
			<s:form name="frm_PayPayroll" id="frm_PayPayroll" action="PayPayroll" theme="simple" method="post">
				<input type="hidden" name="approvePC" value="<%=request.getParameter("paycycle")%>" />
				<s:hidden name="pageFrom" id="pageFrom" />
				<div class="box box-default collapsed-box">
					<div class="box-header with-border">
						<h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
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
									<p style="padding-left: 5px;">Duration</p>
									<s:select theme="simple" name="strPaycycleDuration" id="strPaycycleDuration" listKey="paycycleDurationId" listValue="paycycleDurationName" onchange="submitForm('3', '');" list="paycycleDurationList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organisation</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1', '');" list="organisationList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Paycyle</p>
									<s:select name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerValue="Select Paycycle" list="paycycleList" key="" onchange="submitForm('2', '');" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Payment Mode</p>
									<s:select theme="simple" name="f_paymentMode" id="f_paymentMode" listKey="payModeId" listValue="payModeName" headerKey="" headerValue="All Modes" onchange="submitForm('2', '');" list="paymentModeList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key="" />
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
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Bank</p>
									<s:select theme="simple" name="bankBranch" id="bankBranch" listKey="bankId" listValue="bankName" list="bankBranchList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="hidden" name="filterType" value="filter" />
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2', '');"/>
								</div>
							</div>
						</div> 
	                </div>
	                <!-- /.box-body -->
	            </div>
				<%
					int nPayCount = 0;
					int nPaidCount = 0;
					if (alEarnings.size() > 0) {
				%>
					<div class="row row_without_margin paddingtopbottom10" style="padding-top: 0px;">
						<div class="col-lg-12 col-md-12 col-sm-12" style="text-align: right;">
							<a onclick="generateSalaryExcel();" href="javascript:void(0)" class="excel"></a>
						</div>
					</div>
				
					<div class="row row_without_margin paddingtopbottom10">
						<div class="col-lg-10">
							Choose Bank to pay from:&nbsp;&nbsp;
							<s:select theme="simple" name="bankAccount" id="bankAccount" listKey="bankId" listValue="bankName" list="bankList" key=""/>
							<%if (alEarnings.size() > 0) { %>
								&nbsp;Bifurcate pay out&nbsp;<s:checkbox name="bifurcatePayOut" id="bifurcatePayOut" onclick="checkBifurcatePayOut(this);" />
								<span id="payTypeId">
									&nbsp;Choose Bank Account: <s:select theme="simple" name="bankAccountType" id="bankAccountType" list="#{'1':'Primary','2':'Secondary'}" cssStyle="width: 120px !important;"/>
								</span>
								<span id="unApproveSpan" style="display: none;">
									&nbsp;<input type="button" name="strApprove" class="btn btn-primary disabled" value="PAY"/>
								</span>
								<% if(hmFeatureStatus != null && ((hmFeatureStatus.get(IConstants.F_CANARA_BANK_CODE) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_CANARA_BANK_CODE))) || (hmFeatureStatus.get(IConstants.F_PNB_BANK_CODE) != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_PNB_BANK_CODE))))) { %>
									<span id="sendNeft">
										&nbsp;<%-- <s:submit name="strApprove" cssClass="btn btn-primary" value="PAY" onclick="payForm();"/> --%>
										<input type="button" value="Generate NEFT" name="strNeft" class="btn btn-primary" onclick="exportXLS();"/>
									</span>
								<% } %>
								<span id="approveSpan">
									&nbsp;<%-- <s:submit name="strApprove" cssClass="btn btn-primary" value="PAY" onclick="payForm();"/> --%>
									<input type="button" value="PAY" name="strApprove" class="btn btn-primary" onclick="payForm();"/>
								</span>
							<% } else { %>
								<span id="unApproveSpan"></span>
								<span id="approveSpan"></span>
							<% } %>
						</div>
						
						<div class="col-lg-2">
							<span class="pull-right">Bank Orders: <a href="javascript:void(0)" onclick="donwloadBankStatement('<%=(String)request.getAttribute("f_org")%>','<%=(String)request.getAttribute("paycycle")%>')"><i class="fa fa-files-o" aria-hidden="true"></i></a></span>
						</div>
					</div>
					
					<%-- <% if(pageFrom != null && pageFrom.equals("THREESTEP")) { %>
					<div class="row row_without_margin paddingtopbottom10">
						<div class="col-lg-12">
							<span style="float: right; margin-right: 50px;">
							<span style="float: left; margin-right: 10px"><a href="ApprovePay.action?pageFrom=<%=pageFrom %>"> <%="< Back" %> </a></span> 
							<span style="float: left; margin-right: 10px"><b>Step 3</b></span> 
							<span id="rcfDisableSpan" style="float: left; display: none;">
								<input type="button" class="input_reset" value="Read reports, challans and forms" style="margin: 0px;"/>
							</span>
							<span id="rcfEnableSpan" style="float: left; display: inline;">
								<% if(strBaseUserType != null && strBaseUserType.equals(IConstants.ADMIN)) { %>
									<a href="MenuNavigationInner.action?NN=1109">
								<% } else { %>
									<a href="MenuNavigationInner.action?NN=709">
								<% } %>
								<input type="button" class="input_button" value="Read reports, challans and forms" style="margin: 0px;"/></a>
							</span>
							</span>
						</div>
					</div>
					<% } %> --%>
					
					<div id="bifurcateDivId" style="display: none;">
						<div class="row row_without_margin paddingtopbottom10">
							<div class="col-lg-12">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Select Salary Head</p>
									<s:select theme="simple" name="bifurcateSalaryHead" id="bifurcateSalaryHead" listKey="salaryHeadId" listValue="salaryHeadName" list="salaryHeadList"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<i class="fa fa-long-arrow-right"></i>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">to add in Bank Account</p>
									<s:select theme="simple" name="bifurcateBankAccountType" id="bifurcateBankAccountType" list="#{'1':'Primary','2':'Secondary'}"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Add" value="Add" class="btn btn-primary" onclick="addBifuracateSalaryHead();"/>
								</div>
							</div>
						</div>
						
						<div class="row row_without_margin paddingtopbottom10">
							<div class="col-lg-12">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Primary</p>
									<select name="primarySalaryHead1" id="primarySalaryHead1" multiple="multiple" class="autoHeight"></select>
						    		<input type="hidden" name="primarySalaryHead" id="primarySalaryHead" value=""/>
						    		<i class="fa fa-trash verticalaligntop" onclick="removeBifuracateSalaryHead('1');"></i>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5"></div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Secondary</p>
									<select name="secondarySalaryHead1" id="secondarySalaryHead1" multiple="multiple" class="autoHeight"></select>
						    		<input type="hidden" name="secondarySalaryHead" id="secondarySalaryHead" value=""/>
						    		<i class="fa fa-trash verticalaligntop" onclick="removeBifuracateSalaryHead('2');"></i>
								</div>
							</div>
						</div>					
					</div>
					
					<div class="clr margintop20">
						<table class="table table-bordered overflowtable" id="lt">  <!--  -->
							<thead>
								<tr>
									<th class="alignCenter no-sort" nowrap="nowrap">Action</th>
									<th class="alignCenter" nowrap="nowrap">Employee Code</th>
									<th class="alignCenter" nowrap="nowrap">Employee Name</th>
									<% if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_WORK_LOCATION_IN_APPROVE_PAY_AND_PAY))){ %>
										<th class="alignCenter" nowrap="nowrap">Work Location</th>
									<% } %>
									<th class="alignCenter" nowrap="nowrap">Approve<br /><input type="checkbox" onclick="selectall(this,'chbxApprove')" checked="checked" /></th>
									<th class="alignCenter" nowrap="nowrap">Payment Mode</th>
									<th class="alignCenter" nowrap="nowrap">Net</th>
									<th class="alignCenter" nowrap="nowrap">Gross</th>
	
									<%
										alInnerExport.add(new DataStyle("Salary from "+ request.getAttribute("strD1") + " - "+ request.getAttribute("strD2"),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle("Employee Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle("Employee Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_WORK_LOCATION_IN_APPROVE_PAY_AND_PAY))){
											alInnerExport.add(new DataStyle("Work Location", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										}
										alInnerExport.add(new DataStyle("Approve", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle("Payment Mode", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle("Net", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										alInnerExport.add(new DataStyle("Gross", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									%>
	
									<%
										for (int i = 0; i < alEarnings.size(); i++) {
											alInnerExport.add(new DataStyle(((String) hmSalaryDetails.get((String) alEarnings.get(i)))+ "(+)", Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
											//System.out.println("Earning Head===>"+hmSalaryDetails.get(alEarnings.get(i))+"----head Id===>"+alEarnings.get(i));
									%>
										<th class="alignCenter" nowrap="nowrap">
											<%=(String) hmSalaryDetails.get((String) alEarnings.get(i))%>
											<br />(+)
										</th>
									<% } %>
	
									<% for (int i = 0; i < alDeductions.size(); i++) { %>
									<% if (uF.parseToInt((String) alDeductions.get(i)) == IConstants.LOAN && hmEmpLoan != null) { %>
									
									<% for (int l = 0; l < alLoans.size(); l++) {
										alInnerExport.add(new DataStyle(((String) hmLoanPoliciesMap.get((String) alLoans.get(l)))+ "(-)", Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
									%>
										<th class="alignCenter" nowrap="nowrap">
											<%=hmLoanPoliciesMap.get((String) alLoans.get(l))%>
											<br />(-)
										</th>
									<% } %>
									<% } else {
										alInnerExport.add(new DataStyle(((String) hmSalaryDetails.get((String) alDeductions.get(i)))+ "(-)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									%>
										<th class="alignCenter" nowrap="nowrap">
											<%=(String) hmSalaryDetails.get((String) alDeductions.get(i))%>
											<br />(-)
										</th>
									<% } %>
	
									<% } %>
								</tr>
							<thead>
								<% reportListExport.add(alInnerExport); %>
							
							<tbody>
	
								<%	int j = 0;
		    		
		    			Set set = hmPayPayroll.keySet();
    					Iterator it = set.iterator();
    					while (it.hasNext()) {
    						String strEmpIdWithSalEffectiveDate = (String) it.next();
    						String[] strTemp = strEmpIdWithSalEffectiveDate.split("_");
    						String strEmpId = strTemp[0];
    						Map hmPayroll = (Map) hmPayPayroll.get(strEmpIdWithSalEffectiveDate);
    						if (hmPayroll == null)
    							hmPayroll = new HashMap();
    						
    						j++;
		    		%>
		    				
		    		<%
		    					Map<String, String> hmWLocation = (Map)hmWorkLocationMap.get((String) hmEmpWLocationId.get(strEmpId));
		    		%>	
		    			
		    		<%
		    					alInnerExport = new ArrayList<DataStyle>();
		    				    alInnerExport.add(new DataStyle((String) hmEmpCodeMap.get(strEmpId), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
		    				    alInnerExport.add(new DataStyle((String) hmEmpMap.get(strEmpId), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		    				    if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_WORK_LOCATION_IN_APPROVE_PAY_AND_PAY))){
		    				    	alInnerExport.add(new DataStyle((String) hmWLocation.get("WL_NAME"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		    				    }
		    		%>
		    				
		    				<tr>
		    					<td class="" nowrap="nowrap">
		    						<%if (!uF.parseToBoolean((String) hmIsApprovedSalary.get(strEmpIdWithSalEffectiveDate))) {%>	
			    						<div id="myDiv_<%=j%>" style="float:right">
											<a href="javascript:void(0)" onclick="(confirm('Are you sure you wish to unapprove <%=(String) hmEmpMap.get(strEmpId)%>\'s salary?') ? getContent('myDiv_<%=j%>', 'PayPayroll.action?empId=<%=strEmpId %>&operation=D&paycycle=<%=request.getAttribute("paycycle") %>'):'')">
											<i class="fa fa-trash" aria-hidden="true"></i></a>
										</div>
		    						<%} %>
		    					</td>
		    					<td class="" nowrap="nowrap"><%=(String) hmEmpCodeMap.get(strEmpId)%></td>
		    					<td class="" nowrap="nowrap"><%=(String) hmEmpMap.get(strEmpId)%></td>
		    					<% if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_WORK_LOCATION_IN_APPROVE_PAY_AND_PAY))){ %>
		    						<td class="" nowrap="nowrap"><%=(String) hmWLocation.get("WL_NAME")%></td>
		    					<% } %>
		    					<td class="alignCenter">
		    					<%
		    					    String paidUnpaid = "";
		    					
									if (uF.parseToBoolean((String) hmIsApprovedSalary.get(strEmpIdWithSalEffectiveDate))) {
		    					    	paidUnpaid = "Paid";
		    					    	nPaidCount++;
		    					%>
		    							Paid 
		    					<%
		    					    } else {
		    					    	nPayCount ++;
		    					%>
		    						<input type="checkbox" name="chbxApprove" value="<%=strEmpIdWithSalEffectiveDate%>" checked="checked" />
		    					<%
		    						}
		    						alInnerExport.add(new DataStyle(paidUnpaid, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		    					%>
		    					</td>
		    					
		    					<td class="alignCenter" nowrap="nowrap"><%=(String) hmPayroll.get("PAYMENT_MODE")%></td>
		    					<td class="alignCenter" nowrap="nowrap"><%=uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(roundOffCondition), Math.round(uF.parseToDouble((String) hmPayroll.get("NET"))))))%></td>
		    					<td class="alignCenter" nowrap="nowrap"><%=uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(uF.parseToDouble((String) hmPayroll.get("GROSS"))))))%></td>
		    					<%
		    						alInnerExport.add(new DataStyle((String) hmPayroll.get("PAYMENT_MODE"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
		    						alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition), Math.round(uF.parseToDouble((String) hmPayroll.get("NET")))), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
		    						alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),Math.round(uF.parseToDouble((String) hmPayroll.get("GROSS")))), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
		    					%>
		    					
			    				<%
		    						for (int i = 0; i < alEarnings.size(); i++) {
										alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmPayroll.get((String) alEarnings.get(i)))), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		    					%>
				    					<td class="alignRight" nowrap="nowrap"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmPayroll.get((String) alEarnings.get(i))))%> </td>
					    		<%
					    			}
					    		%>
					    	
					    		<%
	   				    			for (int i = 0; i < alDeductions.size(); i++) {
	   				    				if (uF.parseToInt((String) alDeductions.get(i)) == IConstants.LOAN && hmEmpLoan != null) {
					    					Map hmEmpLoanInner = (Map) hmEmpLoan.get(strEmpIdWithSalEffectiveDate);
					    					if (hmEmpLoanInner == null) hmEmpLoanInner = new HashMap();
					    					for (int l = 0; l < alLoans.size(); l++) {
					    						alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmEmpLoanInner.get((String) alLoans.get(l)))),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					    				%>
					    					<td class="alignRight" nowrap="nowrap"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmEmpLoanInner.get((String) alLoans.get(l))))%> </td>
					    				<%
					    					}
					    				%>		    			
					    			<%
				    					} else {
				    						alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmPayroll.get((String) alDeductions.get(i)))), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				    						    			%>
					    					<td class="alignRight" nowrap="nowrap"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmPayroll.get((String) alDeductions.get(i))))%> </td>
					    			<%
					    				}
					    			%>
					    		<%
					    			}
					    		%>
				    		</tr>
			    			<%
			    				reportListExport.add(alInnerExport);
			    			}
			    			%>
							</tbody>
						</table>
					</div>
				<% } else { %>

				<div class="filter">
					<div class="msg nodata">
						<span> No data available for the current selection </span>
					</div>
				</div>
				<% } %>
				
				<input type="hidden" name="payCount" id="payCount" value="<%=nPayCount %>"/>
				<input type="hidden" name="paidCount" id="paidCount" value="<%=nPaidCount %>"/> 

			</s:form>
			<% session.setAttribute("reportListExport", reportListExport); %>
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
				<div class="modal-body"
					style="height: 400px; overflow-y: auto; padding-left: 25px;">
				</div>
				<div class="modal-footer">
					<button type="button" id="closeButton" class="btn btn-default"
						data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>