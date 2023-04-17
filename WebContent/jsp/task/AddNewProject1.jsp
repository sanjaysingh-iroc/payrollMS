<%@page import="com.konnect.jpms.select.FillSkills"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ page import="java.util.*,com.konnect.jpms.util.UtilityFunctions,com.konnect.jpms.task.*"%>
<jsp:include page="../task/AddProjectValidation.jsp"></jsp:include>

<style>
.greenbox {
	height: 11px;
	background-color: #00FF00; /* the critical component */
}

#redbox {
	height: 11px;
	background-color: #FF0000; /* the critical component */
}

#yellowbox {
	height: 11px;
	background-color: #FFFF00; /* the critical component */
}

.outbox { 
	height: 11px;
	width: 100%;
	background-color: #D8D8D8; /* the critical component */
}

.anaAttrib1 {
	font-size: 12px;
	font-family: digital;
	color: #3F82BF;
	font-weight: bold;
	text-align: center;
	height: 22px;
} 


</style>

<script type="text/javascript">

	function addClient(fromPage) {
		
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Add New Client');
		if($(window).height() >= 500){
      		 $(".modal-body").height(500);
      	 }
		$.ajax({
			url : 'AddClient.action?fromPage='+fromPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
		
	}
	
	
	function editClient(id, proId) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Edit Client');
		$.ajax({
			url : 'AddClient.action?operation=E&ID='+id+'&proId=' +proId+'&fromPage=Project',
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}

	
	function closeForm(pageType) {
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ViewAllProjects.action?pageType='+pageType+'&btnSubmit=Submit',
			success: function(result) {
				$("#divResult").html(result);
	   		}
		});
	}
	
	
	function skipAndProcced(proId, step, pageType) {
		$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PreAddNewProject1.action?operation=E&pro_id='+proId+'&step='+step+'&pageType='+pageType,
			success: function(result) {
				$("#subSubDivResult").html(result);
				
				document.getElementById("proDetail").className = "";
				 document.getElementById("proSnapshot").className = "";
				 document.getElementById("proStep1").className = "";
				 document.getElementById("proStep2").className = "";
				 document.getElementById("proStep3").className = "";
				 document.getElementById("proStep4").className = "";
				 /* document.getElementById("proStep5").className = ""; */
				 document.getElementById("proStep6").className = "";
				 document.getElementById("proStep7").className = "";
				 document.getElementById("proStep8").className = "";
				 var intStep = (parseInt(step)+1);
				 if(step == 4) {
				 	intStep = (parseInt(step)+2);
				 }
				 document.getElementById("proStep"+intStep).className = "active";
	   		}
		});
		/* window.location = "PreAddNewProject1.action?operation=E&pro_id="+proId+"&step="+step+"&pageType="+pageType; */
	}
	
</script>

<% 
	UtilityFunctions uF = new UtilityFunctions();
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	String pro_id = (String)request.getAttribute("pro_id");
	
	String pageType = (String) request.getAttribute("pageType");
	String proType = (String) request.getAttribute("proType");
	//System.out.println("ANP1.jsp/133--proType --->> " + proType+"--pageType="+pageType);
	Map<String, String> hmProInfoDisplay = (Map<String, String>) request.getAttribute("hmProInfoDisplay");
	if(hmProInfoDisplay == null) hmProInfoDisplay = new HashMap<String, String>();

	String userType = (String) session.getAttribute(IConstants.BASEUSERTYPE);
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions); 
	String strProjectName = null;
	String strTitle = "Add New Project";
	if(request.getAttribute("PROJECT_NAME") != null){
		strProjectName = (String)request.getAttribute("PROJECT_NAME");
		strTitle = "Edit Project";
	}
	
	Map<String,String> hmFeatureStatus = (Map<String,String>) request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String,String>();
%>
<!-- <div class="leftbox reportWidth"> -->
<%
	String operation = (String)request.getAttribute("operation");
	String strProOwnerOrTL = (String)request.getAttribute("strProOwnerOrTL");
	
	String step = (String)request.getAttribute("step");
	boolean flag = false;
	if(operation!=null && ((String)operation).length()>0) {
		flag = true;
	}
%>


<s:if test="step==1">
	<script>
	$(function() {
		
		$("#startDate").datepicker({
		    format: 'dd/mm/yyyy',
		    autoclose: true
		}).on('changeDate', function (selected) {
		    var minDate = new Date(selected.date.valueOf());
		    $('#deadline1').datepicker('setStartDate', minDate);
		    //$('#deadline1').datepicker('setDate', minDate);
		});

	    $("#deadline1").datepicker({
			format: 'dd/mm/yyyy',
			autoclose: true
		}).on('changeDate', function (selected) {
	        var minDate = new Date(selected.date.valueOf());
	        $('#startDate').datepicker('setEndDate', minDate);
		});
	/* ===start parvez date: 11-10-2022=== */    
	    $("#strProjectOwner").multiselect().multiselectfilter();
	/* ===end parvez date: 11-10-2022=== */
	
	});

	 $(function() {
		
		if(document.getElementById("strClient")) {
			var strClient = document.getElementById("strClient").value;
			var clientPoc = document.getElementById("clientPoc").value;
			var strClientBrand = document.getElementById("strClientBrand").value;
			//getContent('clientPocTD', 'GetClientPocAjax.action?clientId='+strClient+'&clientPoc='+clientPoc);
			getClientBrandAndSPOCDetails(strClient, strClientBrand, clientPoc);
		}
		<% if(operation == null || !operation.equals("E")) { %>
		if(document.getElementById("organisation")) {
			var orgId = document.getElementById("organisation").value;
			getLocationOrganization(orgId);
		}
		<% } %>
		getExchangeValue();

	});
	
	function checkCosting(id) {
		if (document.getElementById(id).value == 'M') {
			document.getElementById("idIdealTime").innerHTML = "Estimated man-months";
		} else if (document.getElementById(id).value == 'H') {
			document.getElementById("idIdealTime").innerHTML = "Estimated man-hours";
		} else{
			document.getElementById("idIdealTime").innerHTML = "Estimated man-days";
		}
	}
	
	function checkBillingType(id, type) {
		if (document.getElementById(id).value == 'F') {
			document.getElementById("idFBillingAmount").style.display = 'table-row';
			document.getElementById("idFActualBilling").style.display = 'table-row';
			document.getElementById("DailyTR").style.display = 'none';
			checkCosting('idBasisCostingD');
		} else if (document.getElementById(id).value == 'M') {
			document.getElementById("idFBillingAmount").style.display = 'none';
			document.getElementById("idFActualBilling").style.display = 'none';
			document.getElementById("DailyTR").style.display = 'none';
			document.getElementById("idIdealTime").innerHTML = "Estimated man-months";
		} else if (document.getElementById(id).value == 'H') {
			document.getElementById("idFBillingAmount").style.display = 'none';
			document.getElementById("idFActualBilling").style.display = 'none';
			document.getElementById("DailyTR").style.display = 'none';
			document.getElementById("idIdealTime").innerHTML = "Estimated man-hours";
		} else if (document.getElementById(id).value == 'D') {
			document.getElementById("idFBillingAmount").style.display = 'none';
			document.getElementById("idFActualBilling").style.display = 'none';
			document.getElementById("DailyTR").style.display = 'table-row';
			document.getElementById("idIdealTime").innerHTML = "Estimated man-days";
		} else {
			document.getElementById("idFBillingAmount").style.display = 'none';
			document.getElementById("idFActualBilling").style.display = 'none';
			document.getElementById("DailyTR").style.display = 'none';
			document.getElementById("idIdealTime").innerHTML = "Estimated man-days";
		}
		
		var billingType = document.getElementById(id).value;
		getBillingFrequency(billingType);
		getReportCurrSign();
	}
	
	function getReportCurrSign() {
		var currId = document.getElementById("strCurrency").value;
		getContent('reportCurrSignSpan', 'GetReportCurrSign.action?currId='+currId);
	}
	function enableTextfield(value) {
		if(value == '2') {
			document.getElementById("hoursForDay").readOnly = '';
			document.getElementById("hoursForDay").value = '';
			document.getElementById("hoursForDay").setAttribute('class', 'validateRequired');
		} else {
			document.getElementById("hoursForDay").value = '';
			document.getElementById("hoursForDay").readOnly = 'readonly';
			document.getElementById("hoursForDay").setAttribute('class', '');
		}
	}
	
	function ckeckHourRange(value) {
		if(parseFloat(value) < 1 || parseFloat(value) > 24) {
			alert("Please enter hours between 1 to 24");
			document.getElementById("hoursForDay").value = '';
		}
	}
	
	function addResetClient(client) {
		if(client == "0") {
			document.getElementById("strClient").disabled = true;
			document.getElementById("addClientDiv").style.display="none";
			document.getElementById("resetClientDiv").style.display="block";
			document.getElementById("hideStrClient").disabled = '';
			document.getElementById("hideStrClient").value = document.getElementById("strClient").value;
			addClient('Project');
		} else {
			document.getElementById("strClient").disabled = false;
			document.getElementById("hideStrClient").value = '';
			document.getElementById("hideStrClient").disabled = 'disabled';
			document.getElementById("addClientDiv").style.display="block";
			document.getElementById("resetClientDiv").style.display="none";
		}
	}
	
	function editResetClient(client, proId) {
		if(client == "0") {
			document.getElementById("strClient").disabled = true;
			document.getElementById("addClientDiv").style.display="none";
			document.getElementById("resetClientDiv").style.display="block";
			document.getElementById("hideStrClient").disabled = '';
			var clientId = document.getElementById("strClient").value;
			document.getElementById("hideStrClient").value = clientId;
			editClient(clientId, proId);
		} else {
			document.getElementById("strClient").disabled = false;
			document.getElementById("hideStrClient").value = '';
			document.getElementById("hideStrClient").disabled = 'disabled';
			document.getElementById("addClientDiv").style.display="block";
			document.getElementById("resetClientDiv").style.display="none";
		}
	}
	
	function getLocationOrganization(orgid) {
		var action='GetWorkLocationByOrganization.action?strOrg='+orgid ;
		getContent('locationdivid', action);
		window.setTimeout( 
		    function() {
		    	getSBUByOrganization(orgid);
		    }, 200);
		window.setTimeout( 
		    function() {
		    	getDepartmentByOrganization(orgid);
		    }, 300);
		 window.setTimeout( 
		    function() {
		    	getContent('reportCurrSpan', 'GetOrganizationCurrency.action?orgId='+orgid+'&from=AddPro&type=R');
				getContent('billingCurrSpan', 'GetOrganizationCurrency.action?orgId='+orgid+'&from=AddPro&type=B');
		    }, 500);
	}
	
	function getSBUByOrganization(orgid) {
		var action='GetSBUByOrganization.action?strOrg='+orgid;
		getContent('sbudivid', action);
	}
	
	function getDepartmentByOrganization(orgid) {
		var action='GetDepartmentByOrganization.action?strOrg='+orgid;
		getContent('departmentdivid', action);
	}
	
	function getExchangeValue() {
		var strCurrency = document.getElementById("strCurrency").value;
		var strBillingCurrency = document.getElementById("strBillingCurrency").value;
		 if(strBillingCurrency == strCurrency || strBillingCurrency == '' || strBillingCurrency == '0') {
			 document.getElementById("exchangevalueSpan1").innerHTML = "";
		 } else {
		 	getContent('exchangevalueSpan1', 'ViewAndUpdateExchangeValue.action?strBillingCurrId='+strBillingCurrency+'&strReportCurrId='+strCurrency+'&type=ExV');
		 }
		 window.setTimeout( 
		    function() {
		    	calBillingAmount();
		    }, 700);
	}

	function calBillingAmount() {
		var billingAmt = document.getElementById("billingAmountF").value;
		var strBillingCurrency = document.getElementById("strBillingCurrency").value;
		var strCurrency = document.getElementById("strCurrency").value;
		if(strBillingCurrency == strCurrency || strBillingCurrency == '' || strBillingCurrency == '0') {
			document.getElementById("exchangeAmountSpan").innerHTML = '';
		} else {
			var exchangeValueHide = document.getElementById("exchangeValueHide").value;
			var otherCurrVal = 0;
			if(billingAmt == '') {
				billingAmt = 0;
			}
			if(parseFloat(exchangeValueHide) > 0) {
				otherCurrVal = parseFloat(billingAmt) * parseFloat(exchangeValueHide);
			}
			document.getElementById("exchangeAmountSpan").innerHTML = 'Billing Amount: ' + otherCurrVal.toFixed(1);
		}
	}
	
	function getBillingFrequency(billingType) {
		var	strBillingKind = document.getElementById("strBillingKind").value;
		var action='GetBillingFrequency.action?billingType='+billingType+'&strBillingKind='+strBillingKind;
		getContent('billFreqSpan', action);
	}
	function hideShowMilestone(value, type) {
		getBillingCycle(value, type);
	}
	
	function getBillingCycle(billingFreq, type) {
		if(billingFreq == 'W') {
			document.getElementById("billingCycle").style.display="block";
			document.getElementById("weekly").style.display="block";
			if(type == '') {
				document.getElementById("weekdayCycle").selectedIndex = "0";
				document.getElementById("dayCycle").selectedIndex = "0";
			}
			document.getElementById("monthly").style.display="none";
		} else if(billingFreq == 'B') {
			document.getElementById("billingCycle").style.display="block";
			document.getElementById("monthly").style.display="block";
			if(type == '') {
				document.getElementById("weekdayCycle").selectedIndex = "0";
				document.getElementById("dayCycle").selectedIndex = "0";
			}
			document.getElementById("weekly").style.display="none";
		} else if(billingFreq == 'M') {
			document.getElementById("billingCycle").style.display="block";
			document.getElementById("monthly").style.display="block";
			if(type == '') {
				document.getElementById("weekdayCycle").selectedIndex = "0";
				document.getElementById("dayCycle").selectedIndex = "0";
			}
			document.getElementById("weekly").style.display="none";
		}
		else if(billingFreq == 'F' || billingFreq == 'F') {
			
			document.getElementById("billingCycle").style.display="none";
			document.getElementById("monthly").style.display="none";
			if(type == '') {
				document.getElementById("weekdayCycle").selectedIndex = "0";
				document.getElementById("dayCycle").selectedIndex = "0";
			}
			document.getElementById("weekly").style.display="none"; 
		} 
		else {
			if(type == '') {
				document.getElementById("weekdayCycle").selectedIndex = "0";
				document.getElementById("dayCycle").selectedIndex = "0";
			}
			document.getElementById("billingCycle").style.display="none";
			document.getElementById("weekly").style.display="none";
			document.getElementById("monthly").style.display="none";
		}
	}
	

	function getProjectOrgDetails(proOwnerId) {
		xmlhttp = GetXmlHttpObject();
	    if (xmlhttp == null) {
	            alert("Browser does not support HTTP Request");
	            return;
	    } else {
            var xhr = $.ajax({
                 url : "GetProjectOwnerOrgDetails.action?proOwnerId=" + proOwnerId,
                 cache : false,
                 success : function(data) {
                 	if(data == ""){
                 	}else{
                 		var allData = data.split("::::");
                        document.getElementById("orgdivid").innerHTML = allData[0];
                        document.getElementById("locationdivid").innerHTML = allData[1];
                        document.getElementById("sbudivid").innerHTML = allData[2];
                        document.getElementById("departmentdivid").innerHTML = allData[3];
                 	}
                 }
            });
	    }
	}

	
	function getClientBrandAndSPOCDetails(clientId, strClientBrand, clientPoc) {
		xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return;
	    } else {
			var xhr = $.ajax({
				url : "GetClientPocAjax.action?clientId="+clientId+"&strClientBrand="+strClientBrand+"&clientPoc="+clientPoc,
				cache : false,
				success : function(data) {
                	if(data == "") {
                	} else {
                		var allData = data.split("::::");
                		//alert("allData.length ===>> " +allData.length); 
                		if(allData.length>1) {
                			document.getElementById("clientBrandTR").style.display= 'table-row';
							document.getElementById("clientBrandTD").innerHTML = allData[0];
							document.getElementById("clientPocTD").innerHTML = allData[1];
						} else {
							document.getElementById("clientBrandTR").style.display= 'none';
							document.getElementById("clientPocTD").innerHTML = allData[0];
                 		}
					}
				}
			});
		}
	}
	
	
	function GetXmlHttpObject() {
	    if (window.XMLHttpRequest) {
	            return new XMLHttpRequest();
	    }
	    if (window.ActiveXObject) {
	            return new ActiveXObject("Microsoft.XMLHTTP");
	    }
	    return null;
	}
	
	
function submitAndProcced(formId, step) {
		
		if(checkValidationStep1()) {
			if(document.getElementById("submitBtnTable_"+step)) {
				document.getElementById("submitBtnTable_"+step).style.display = "none";
			}
			var proType = document.getElementById("proType").value;
			var form_data = $("#"+formId).serialize();
			$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'PreAddNewProject1.action?submit=SubmitAndProceed',
				data: form_data,
				success: function(result) {
					$("#subSubDivResult").html(result);
					if(step == 1) {
						document.getElementById("proDetail").style.display = 'block';
						if(proType==null || proType== 'L') {
							document.getElementById("proSnapshot").style.display = 'block';
						}
						document.getElementById("proStep2").style.display = 'block';
						if(proType==null || proType== 'L') {
							document.getElementById("proStep3").style.display = 'block';
							document.getElementById("proStep4").style.display = 'block';
							/* document.getElementById("proStep5").style.display = 'block'; */
							document.getElementById("proStep6").style.display = 'block';
							document.getElementById("proStep7").style.display = 'block';
							document.getElementById("proStep8").style.display = 'block';
						}
					}
					document.getElementById("proDetail").className = "";
					document.getElementById("proSnapshot").className = "";
					document.getElementById("proStep1").className = "";
					document.getElementById("proStep2").className = "";
					document.getElementById("proStep3").className = "";
					document.getElementById("proStep4").className = "";
					document.getElementById("proStep6").className = "";
					document.getElementById("proStep7").className = "";
					document.getElementById("proStep8").className = "";
					var intStep = (parseInt(step)+1);
					 if(step == 4) {
					 	intStep = (parseInt(step)+2);
					 }
					document.getElementById("proStep"+intStep).className = "active";
		   		}
			});
		}
	}
	
	
	function saveAndExit(formId, step) {
		
		if(checkValidationStep1()) {
			if(document.getElementById("submitBtnTable_"+step)) {
				document.getElementById("submitBtnTable_"+step).style.display = "none";
			}
			var proType = document.getElementById("proType").value;
			var form_data = $("#"+formId).serialize();
			$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'PreAddNewProject1.action?submit=SaveAndExit',
				data: form_data,
				success: function(result) {
					getAllProjectNameList('AllProjectNameList', proType);
				}
			});
		}
	}
	
</script>

	<s:form id="frmAddProject_1" cssClass="formcss"
		action="PreAddNewProject1" name="frmAddProject_1" method="post"
		enctype="multipart/form-data" theme="simple" required="required">
		<div class="row row_without_margin">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<s:hidden name="step" value="1"></s:hidden>
				<s:hidden name="operation"></s:hidden>
				<s:hidden name="pageType"></s:hidden>
				<s:hidden name="proType" id="proType"></s:hidden>
				<table border="0" class="table table_no_border">
					<%if((String)request.getAttribute("pro_id")!=null) { %>
					<s:hidden name="pro_id"></s:hidden>
					<% } %>
					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">Project Name:<sup>*</sup>
						</td>
						<td colspan="2"><s:textfield id="prjectname" cssClass="validateRequired" name="prjectname" label="Project Name" required="required" /></td>
					</tr>
				<!-- ===start parvez date: 21-11-2022=== -->	
					<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_ADDITIONAL_DETAILS_IN_PROJECT_CREATION))){ %>
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight">Project ID:<sup>*</sup>
							</td>
							<td colspan="2"><s:textfield id="prjectCode" cssClass="validateRequired" name="prjectCode" label="Project ID" required="required" /></td>
						</tr>
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight">UST Project ID:<sup>*</sup>
							</td>
							<td colspan="2"><s:textfield id="ustPrjectId" cssClass="validateRequired" name="ustPrjectId" label="UST Project ID" required="required" /></td>
						</tr>
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight">Account ID:<sup>*</sup>
							</td>
							<td colspan="2"><s:textfield id="projectAccountId" cssClass="validateRequired" name="projectAccountId" label="Project Account ID" required="required" /></td>
						</tr>
						
					<% } %>
				<!-- ===end parvez date: 21-11-2022=== -->	
					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">Client:<sup>*</sup>
						</td>
						<td colspan="2"><span style="float: left;"> <s:select theme="simple" label="Select Client" name="strClient" id="strClient" listKey="clientId" cssClass="validateRequired"
									headerKey="" headerValue="Select Client" listValue="clientName" list="clientList" onchange="getClientBrandAndSPOCDetails(this.value, '', '');" />
						</span>
							<div style="float: left; margin-left: 20px;">
								<span id="addClientDiv"> <%
								//String pro_id = (String)request.getAttribute("pro_id");
								if(userType != null && (userType.equals(IConstants.ADMIN) || userType.equals(IConstants.HRMANAGER))) {
									if(operation != null && operation.equals("E")) { %>
									<a href="javascript:void(0)" onclick="editResetClient('0','<%=pro_id %>');"> <u>Edit Customer</u> </a> <% } else { %> 
									<a href="javascript:void(0)" onclick="addResetClient('0');"> <u>Add New Customer</u> </a> <% } %>
									<% } %> </span> <span id="resetClientDiv" style="display: none;">
									<%if(operation != null && operation.equals("E")) { %> 
									<a href="javascript:void(0)" onclick="editResetClient('1','<%=pro_id %>');"><u>Reset</u></a>
									<input type="hidden" name="strClient" id="hideStrClient" disabled="disabled" /> <% } else { %> 
									<a href="javascript:void(0)" onclick="addResetClient('1');"><u>Reset</u></a>
									<input type="hidden" name="strClient" id="hideStrClient" disabled="disabled" /> <% } %> </span>
							</div>
						</td>
					</tr>
					<% 
					String strBrandStatus = "none";
					if(uF.parseToInt((String)request.getAttribute("strClientBrand"))>0) {
						strBrandStatus = "";
					}
						%>
					
					<tr id="clientBrandTR" style="display: <%=strBrandStatus %>;">
						<td nowrap="nowrap" class="txtlabel alignRight">Subsidiary/ Brand:<sup>*</sup></td>
						<td colspan="2" id="clientBrandTD">
							<s:select theme="simple" label="Select Subsidiary/ Brand" name="strClientBrand" id="strClientBrand" listKey="clientBrandId" cssClass="validateRequired"
								headerKey="" headerValue="Select Client" listValue="clientBrandName" list="clientBrandList" onchange="getContent('clientPocTD', 'GetClientPocAjax.action?strClientBrand='+this.value)" />
						</td>
					</tr>
					
					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">SPOC:<sup>*</sup></td>
						<td colspan="2" id="clientPocTD">
							<s:select theme="simple" name="clientPoc" id="clientPoc" cssClass="validateRequired" listKey="clientPocId" listValue="clientPocName" headerKey="" 
								headerValue="Select SPOC" list="clientPocList" />
						</td>
					</tr>

					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">Referenced By:</td>
						<td><s:select label="Select Project Owner" name="strReferanceBy" listKey="employeeId" headerKey="" headerValue="Select Referance By" listValue="employeeName"
								list="projectReferanceList" onchange="getProjectOrgDetails(this.value);" /></td>
					</tr>

					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">Project Owner:<sup>*</sup>
						</td>
				<!-- ===start parvez date: 11-10-2022=== -->		
						<td><s:select name="strProjectOwner" id="strProjectOwner" listKey="employeeId" cssClass="validateRequired" headerKey=""
								headerValue="Select Project Owner" listValue="employeeName" list="projectOwnerList" onchange="getProjectOrgDetails(this.value);" multiple="true" /></td>
				<!-- ===end parvez date: 11-10-2022=== -->				
					</tr>
					
			<!-- ===start parvez date: 11-10-2022=== -->
					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">Portfolio Manager:
						</td>		
						<td><s:select name="strPortfolioManager" id="strPortfolioManager" listKey="employeeId" headerKey=""
								headerValue="Select Portfolio Manager" listValue="employeeName" list="portfolioManagerList" onchange="getProjectOrgDetails(this.value);" /></td>	
					</tr>
					
					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">Account Manager:
						</td>		
						<td><s:select name="strAccountManager" id="strAccountManager" listKey="employeeId" headerKey=""
								headerValue="Select Account Manager" listValue="employeeName" list="accountManagerList" onchange="getProjectOrgDetails(this.value);" /></td>	
					</tr>
					
					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">Delivery Manager:
						</td>		
						<td><s:select name="strDeliveryManager" id="strDeliveryManager" listKey="employeeId" headerKey=""
								headerValue="Select Delivery Manager" listValue="employeeName" list="deliveryManagerList" onchange="getProjectOrgDetails(this.value);" /></td>	
					</tr>		
			<!-- ===end parvez date: 11-10-2022=== -->		
					
					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">Service:<sup>*</sup>
						</td>
						<td><s:select name="service" id="service" listKey="serviceId" cssClass="validateRequired" headerKey="" headerValue="Select Service" listValue="serviceName" list="serviceList" /></td>
					</tr>
					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">Organization:<sup>*</sup>
						</td>
						<td><div id="orgdivid">
								<s:select theme="simple" name="organisation" id="organisation" cssClass="validateRequired" listKey="orgId" listValue="orgName" list="organisationList" onchange="getLocationOrganization(this.value);" />
							</div>
						</td>
					</tr>
					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">Work Location:<sup>*</sup></td>
						<td>
							<div id="locationdivid">
								<s:select cssClass="validateRequired" name="location" theme="simple" listKey="wLocationId" listValue="wLocationName" list="workLocationList" />
							</div></td>
					</tr>
					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">SBU:<sup>*</sup>
						</td>
						<td>
							<div id="sbudivid">
								<s:select name="strSBU" id="strSBU" listKey="serviceId" cssClass="validateRequired" listValue="serviceName" list="sbuList" key="" required="true" />
							</div>
						</td>
					</tr>
					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">Department:<sup>*</sup>
						</td>
						<td>
							<div id="departmentdivid">
								<s:select name="strDepartment" listKey="deptId" cssClass="validateRequired" listValue="deptName" list="departmentList" key="" required="true" />
							</div>
						</td>
					</tr>
				<!-- ===start parvez date: 21-11-2022=== -->	
					<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_ADDITIONAL_DETAILS_IN_PROJECT_CREATION))){ %>
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight">Segment:<sup>*</sup>
							</td>
							<td colspan="2"><s:textfield id="projectSegment" cssClass="validateRequired" name="projectSegment" label="Project Segment" required="required" /></td>
						</tr>
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight">Domain:
							</td>		
							<td><s:select name="proDomain" id="proDomain" listKey="domainId" headerKey=""
									headerValue="Select Domain" listValue="domainName" list="projectDomainList" /></td>	
						</tr>
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Description:</td>
							<td colspan="2"><s:textarea name="shortDescription" cols="50" rows="2" /></td>
						</tr>
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Note:</td>
							<td colspan="2"><s:textarea name="description" cols="50" rows="05" /></td>
						</tr>
						
					<% } else{ %>
			<!-- ===end parvez date: 21-11-2022=== -->		
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Short Description:</td>
							<td colspan="2"><s:textarea name="shortDescription" cols="50" rows="2" /></td>
						</tr>
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Long Description:</td>
							<td colspan="2"><s:textarea name="description" cols="50" rows="05" /></td>
						</tr>
					<% } %>
					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">Overall Project Priority:<sup>*</sup>
						</td>
						<td><s:select label="Select Priority" name="priority" id="priority" cssClass="validateRequired" listKey="priId" listValue="proName" list="priorityList" key="" />
						</td>
					</tr>
					<tr>
						<td nowrap="nowrap" class="txtlabel alignRight">Start Date:<sup>*</sup>
						</td>
						<td><s:textfield id="startDate" name="startDate" cssClass="validateRequired" required="true" /></td>
					</tr>
					<tr>
						<td valign="top" nowrap="nowrap" class="txtlabel alignRight">End Date / Deadline:<sup>*</sup></td>
						<td><s:textfield id="deadline1" name="deadline" cssClass="validateRequired" required="true" />
							<div style="font-style: italic; color: gray;">deadline in case of fixed billing type</div></td>
					</tr>
					
					<% if(uF.parseToInt(strProOwnerOrTL) == 2 && uF.parseToInt(pro_id) > 0 ) { //&& getPageType() != null && getPageType().equals("MP") %>
					<% } else { %>
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight">Choose Reporting Currency:<sup>*</sup></td>
							<td><span id="reportCurrSpan">
									<s:select id="strCurrency" cssClass="validateRequired" name="strCurrency" listKey="currencyId" listValue="currencyName" headerKey=""
										headerValue="Select Currency" list="currencyList" onchange="getExchangeValue();" />
								</span>
							</td>
						</tr>
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight">Choose Billing Currency:<sup>*</sup></td>
							<td><span id="billingCurrSpan" style="float: left;">
									<s:select theme="simple" id="strBillingCurrency" cssClass="validateRequired" name="strBillingCurrency" listKey="currencyId" 
										listValue="currencyName" headerKey="" headerValue="Select Billing Currency" list="currencyList" onchange="getExchangeValue();" />
								</span>
								<div style="float: left; margin-left: 7px; margin-top: 2px;" id="exchangevalueSpan1"></div>
							</td>
						</tr>
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight">Project Billing Type:<sup>*</sup></td>
							<td><s:select name="billingType" listKey="billingId" cssClass="validateRequired" headerKey="" headerValue="Select Billing Type" 
									listValue="billingName" id="idBilling" list="billingList" key="" required="true" onchange="checkBillingType(this.id, '')" />
							</td>
						</tr>
						<tr id="DailyTR" style="display: none;">
							<td nowrap="nowrap" class="txtlabel alignRight">&nbsp;</td>
							<td colspan="2">
								<% 
									String hoursForDay = (String) request.getAttribute("hoursForDay");
									String hoursToDay = (String) request.getAttribute("hoursToDay");
									String hoursToDay1 = "";
									String hoursToDay2 = "";
									String hoursForDayR = "readonly='readonly'";
									String hoursForDayValid = "";
									if(hoursToDay != null && hoursToDay.equals("1")) {
										hoursToDay1 = "checked='checked'";
									} else if(hoursToDay != null && hoursToDay.equals("2")) {
										hoursToDay2 = "checked='checked'";
										hoursForDayR = "";
										hoursForDayValid = "class='validateRequired'";
									}
								%> <span style="width: 100%; float: left;">
										<input type="radio" name="hoursToDay" id="hoursToDay" value="1" <%=hoursToDay1 %> onclick="enableTextfield(this.value)">
										If Daily, one single hr entry is considered 1 day<br> 
										<i>If multiple tasks are taken up in one day, the task hrs are equally distributed</i> </span>
										<span style="width: 100%; float: left; margin-top: 10px;"> <input type="radio" name="hoursToDay" id="hoursToDay" value="2" <%=hoursToDay2 %> onclick="enableTextfield(this.value)">
										If Daily, convert no. of hrs to Days. One Day = <input type="text" name="hoursForDay" id="hoursForDay" style="width: 30px !important;" onkeyup="ckeckHourRange(this.value)" 
											onkeypress="return isNumberKey(event)" <%=hoursForDayValid %> value="<%=uF.showData(hoursForDay, "") %>" <%=hoursForDayR %> /> hrs </span>
							</td>
							<td></td>
						</tr>
						<% String billingType=(String)request.getAttribute("billingType"); %>
						<tr id="idFBillingAmount"
							<% if(billingType==null || billingType.equals("H") || billingType.equals("D") || billingType.equals("M")) { %>
							style="display: none;" <% } %>>
							<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Enter Fixed Amount:<sup>*</sup></td>
							<td>
								<div style="float: left;">
						<!-- ===start parvez date: 16-11-2021=== -->
									<s:textfield cssClass="validateRequired" name="billingAmountF" id="billingAmountF" required="true" onkeyup="calBillingAmount();" onkeypress="return isNumberKey(event)"/>
						<!-- ===end parvez date: 16-11-2021=== -->
									<span style="float: right; margin-left: 5px;" id="reportCurrSignSpan"><%=uF.showData((String)request.getAttribute("currSign"), "") %></span>
								</div>
								<div style="float: left; margin-left: 7px; margin-top: 2px;" id="exchangeAmountSpan"></div>
							</td>
							<td></td>
							<td></td>
						</tr>
	
						<tr id="idFActualBilling" <% if(billingType==null || billingType.equals("H") || billingType.equals("D") || billingType.equals("M")) { %> style="display: none;" <% } %>>
							<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Select Basis of Assignment Costing:<sup>*</sup>
							</td>
							<td><s:radio cssClass="validateRequired" name="strActualBilling" id="idBasisCosting" onclick="checkCosting(this.id)" list="#{'D':'Daily','H':'Hourly','M':'Monthly'}" /></td>
							<td></td>
							<td></td>
						</tr>
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight" valign="top"><span id="idIdealTime">Estimated man-hours or man-days</span>:</td>
					<!-- ===start parvez date: 15-11-2021=== -->
							<td><s:textfield name="estimatedHours" required="true" onkeypress="return isNumberKey(event)" /></td>
					<!-- ===end parvez date: 15-11-2021=== -->
							<td></td>
							<td></td>
						</tr>
						<tr>
							<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Select Billing Frequency:<sup>*</sup>
							</td>
							<td><span id="billFreqSpan" style="float: left;">
								<s:select name="strBillingKind" id="strBillingKind" listKey="billingId" cssClass="validateRequired" headerKey="" headerValue="Select Billing Frequency" 
									listValue="billingName" list="billingKindList" key="" required="true" onchange="hideShowMilestone(this.value,'');" /> </span>
							</td>
							<td nowrap="nowrap" class="txtlabel alignRight" valign="top"><span id="billingCycle" style="display: none;">Billing Cycle:<sup>*</sup></span></td>
	
							<td><span id="weekly" style="display: none; margin-left: 15px;">Day: <s:select theme="simple" name="weekdayCycle" id="weekdayCycle"
									cssStyle="width:100px !important;" headerKey="" headerValue="Select Day" cssClass="validateRequired"
									list="#{'Monday':'Monday','Tuesday':'Tuesday', 'Wednesday':'Wednesday','Thursday':'Thursday','Friday':'Friday','Saturday':'Saturday','Sunday':'Sunday'}" />
							</span> <span id="monthly" style="display: none; margin-left: 15px;">Date of Month: <s:select theme="simple" name="dayCycle" id="dayCycle"
										cssStyle="width:100px !important;" headerKey="" headerValue="Date" cssClass="validateRequired"
										list="#{'1':'1', '2':'2', '3':'3', '4':'4', '5':'5', '6':'6', '7':'7', '8':'8', '9':'9', '10':'10', '11':'11', 
	                                   '12':'12', '13':'13', '14':'14', '15':'15', '16':'16', '17':'17', '18':'18', '19':'19', '20':'20', '21':'21', 
	                                   '22':'22', '23':'23', '24':'24', '25':'25', '26':'26', '27':'27', '28':'28', '29':'29', '30':'30'}" />
							</span></td>
						</tr>
						<% if(proType!=null && !proType.equals("") && !proType.equalsIgnoreCase("null") && proType.equals("P")){ %>
							<tr>
								<td nowrap="nowrap" class="txtlabel alignRight">Revenue Target:
								</td>
								<td colspan="2"><s:textfield id="revenueTarget" name="revenueTarget" label="Revenue Target" /></td>
							</tr>
						<% } %>
					<% } %>
					
				</table>
			</div>
		</div>

		<div class="clr"></div>
		<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L") || proType.equalsIgnoreCase("P")) { %>
		<div style="margin: 0px 0px 0px 120px">
			<table id="submitBtnTable_1" border="0" class="table table_no_border">
				<tr>
					<td>
						<% if(operation != null && operation.equals("E")) { %> 
							<input type="button" name="skipProcced" value="Skip & Proceed" class="btn btn-primary" style="float: right; margin-right: 5px;" onclick="skipAndProcced('<%=request.getAttribute("pro_id") %>', '1', '<%=pageType %>');" />
						<% } %>
						<input type="submit" name="submit" id="btnOk" value="Submit & Proceed" class="btn btn-primary cstm-validate" style="float: right; margin-right: 5px;" onclick="submitAndProcced('frmAddProject_1', '1');" /> 
						<input type="submit" name="stepSave" id="btnOkExit" value="Save & Exit" class="btn btn-primary cstm-validate" style="float: right; margin-right: 5px;" onclick="saveAndExit('frmAddProject_1', '1');" /> <!--  --> 
						<input type="button" value="Cancel" class="btn btn-danger" style="float: right; margin-right: 5px;" name="cancel" onclick="closeForm('<%=pageType %>');"></td>
				</tr>
			</table>
		</div>
		<% } %>
	</s:form>

	<script>
	
	/* $("#btnOk").click(function(){
		$('.validateRequired').filter(':hidden').prop('required',false);
		$('.validateRequired').filter(':visible').prop('required',true);
		$('.validateEmail').filter(':visible').attr('type','email');
    }); */
	
		$(".cstm-validate").click(function(){
			$('.validateRequired').filter(':hidden').prop('required',false);
			$('.validateRequired').filter(':visible').prop('required',true);
		});
    
	</script>

</s:if>
<s:if test="step==2">

	<style>
		.formcss select,.formcss button {
			width: 150px !important;
		}
	</style>

	<script>
	var tl = '<%=(String)request.getAttribute("TL") %>';
	var tm = '<%=(String)request.getAttribute("TM") %>';
	var projId = '<%=(String)request.getAttribute("pro_id")%>';
	
	jQuery(document).ready(function() {
		if(document.getElementById("f_strWLocation")) {
			$("#f_strWLocation").multiselect().multiselectfilter();
		}
		if(document.getElementById("strWLocation0")) {
			$("#strWLocation0").multiselect().multiselectfilter();
		}
    	/* $("#f_department").multiselect().multiselectfilter();
    	$("#f_level").multiselect().multiselectfilter(); */
    	if(document.getElementById("skill")) {
    		$("#skill").multiselect().multiselectfilter();
    	}
    	/* jQuery("#formID_2").validationEngine(); */
    	if(document.getElementById("projectId") && document.getElementById("projectId").value == '') {
			document.getElementById("projectId").value = projId;
		}
		getTeam();
	});

	function getTeam() {
		var strLocation = '';
		if(document.getElementById("f_strWLocation")) {
			strLocation = getSelectedValue("f_strWLocation");
		}
		//var strLevel = getSelectedValue("f_level");
		//var strDepartment = getSelectedValue("f_department");
		var strSkill = '';
		if(document.getElementById("skill")) {
			strSkill = getSelectedValue("skill");
		}
		$("#teamLeadListID").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		getContent('teamLeadListID', 'GetEmployeeListAjax1.action?strSkill='+strSkill+'&strLocation='+strLocation+'&pro_id=<%=request.getAttribute("pro_id")%>&operation=<%=request.getParameter("operation")%>'
			+'&step=2&pageType=<%=pageType %>&proType=<%=proType %>');
		//+'&strLevel='+strLevel+'&strDepartment='+strDepartment
	}
	
	
	function getSelectedValue(selectId) {
		var exportchoice = "";
		if(document.getElementById(selectId)) {
			var choice = document.getElementById(selectId);
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
		}
    	return exportchoice;
    }
	
	
	var myVar; 
	function setTeamMembers(){
		var myVar1 = document.getElementById('teamLeadListID');
		if(myVar1 && myVar1.innerHTML.length>0){
			var a=tm.split(",");
	    	var b=tl.split(",");
	    	for(i=0;i<a.length;i++){
	    		$("#strEmpId"+a[i]).attr('checked',true);
	    	}
			for(i=0;i<b.length;i++){
				$("#strTeamLeadId"+b[i]).attr('checked',true);						
			}	
			window.clearInterval(myVar);
		}
	}
	
	
	function resizeIframe(obj) {
		obj.style.height = obj.contentWindow.document.body.scrollHeight + 'px';
	}

	
	var sbSkills = '<%=(String) request.getAttribute("sbSkills") %>';
	var sbWLocs = '<%=(String) request.getAttribute("sbWLocs") %>';
	function addNewSkill() {
		var skillCnt = document.getElementById("skillCount").value;
		var cnt = (parseInt(skillCnt)+1);
	    var table = document.getElementById("tblResourceRequirement");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    row.id="skillTR"+cnt;
	    /* "+proStartDate+" "+proEndDate+" */
	    var cell0 = row.insertCell(0);
	    cell0.innerHTML = "<input type=\"hidden\" name=\"skillTRId\" id=\"skillTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"hidden\" name=\"proResourceReqId\" id=\"proResourceReqId"+cnt+"\" value=\"0\">"+
	    "<select name=\"strWLocation"+cnt+"\" id=\"strWLocation"+cnt+"\" style=\"width:160px !important;\" multiple=\"multiple\" >"+
	    sbWLocs+"</select>";
	    
	    var cell1 = row.insertCell(1);
	    cell1.innerHTML = "<select name=\"requiredSkill\" id=\"requiredSkill"+cnt+"\" class=\"validateRequired\" style=\"width:160px !important; \" >"+
		"<option value=''>Select Skill</option>"+
	    sbSkills+"</select>";
		
	    var cell2 = row.insertCell(2);
	    cell2.innerHTML = "<input type=\"text\" id=\"reqMinExp"+cnt+"\" name=\"reqMinExp\" class=\"validateRequired\" placeholder=\"min yrs\" style=\"width:50px !important;\" onkeypress=\"return isNumberKey(event);\"> - "+
	    "<input type=\"text\" id=\"reqMaxExp"+cnt+"\" name=\"reqMaxExp\" class=\"validateRequired\" placeholder=\"max yrs\" style=\"width:50px !important;\" onkeypress=\"return isNumberKey(event);\">";
		
		var cell3 = row.insertCell(3);
		cell3.innerHTML = "<input type=\"text\" id=\"reqResource"+cnt+"\" name=\"reqResource\" class=\"validateRequired\" style=\"width:50px !important;\" onkeypress=\"return isNumberKey(event);\" onkeyup=\"setReqResToGap('"+cnt+"');\"> ";
		
		var cell4 = row.insertCell(4);
		cell4.innerHTML = "<input type=\"text\" id=\"reqResourceGap"+cnt+"\" name=\"reqResourceGap\" style=\"width:50px !important;\" readonly=\"readonly\"> ";
		
		var cell5 = row.insertCell(5);
		cell5.innerHTML = "<a href=\"javascript:void(0);\" onclick=\"deleteSkill('"+cnt+"','')\"><i class=\"fa fa-times\"></i></a>";

	    document.getElementById("skillCount").value = cnt;
	    $("#strWLocation"+cnt).multiselect().multiselectfilter();
	}
	
	
	function setReqResToGap(count) {
		document.getElementById("reqResourceGap"+count).value = document.getElementById("reqResource"+count).value;
	}
	
	function deleteSkill(count, proResReqId) {
		if(confirm('Are you sure, you want to delete this skill?')) {
			var trIndex = document.getElementById("skillTR"+count).rowIndex;
		    document.getElementById("tblResourceRequirement").deleteRow(trIndex);
		    deleteSkillReqFromDB(proResReqId);
		}
	}

	function deleteSkillReqFromDB(proResReqId) {
		getContent('', 'EditEmpRateAndCost.action?submitType=DELETESRR&proResReqId='+proResReqId);
	}
	
	function createResourceRequirement(formId, step) {
		if(document.getElementById("submitBtnDiv_"+step)) {
			document.getElementById("submitBtnDiv_"+step).style.display = "none";
		}
		var form_data = $("#"+formId).serialize();
		$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PreAddNewProject1.action?submit=CreateResReq',
			data: form_data,
			success: function(result) {
				$("#subSubDivResult").html(result);
				document.getElementById("proDetail").className = "";
				if(document.getElementById("proSnapshot"))
				document.getElementById("proSnapshot").className = "";
				document.getElementById("proStep1").className = "";
				document.getElementById("proStep2").className = "";
				document.getElementById("proStep3")
				document.getElementById("proStep3").className = "";
				document.getElementById("proStep4")
				document.getElementById("proStep4").className = "";
				/* document.getElementById("proStep5").className = ""; */
				document.getElementById("proStep6")
				document.getElementById("proStep6").className = "";
				document.getElementById("proStep7")
				document.getElementById("proStep7").className = "";
				document.getElementById("proStep8")
				document.getElementById("proStep8").className = "";
				document.getElementById("proStep"+step).className = "active";
	   		}
		});
	}
	
	function submitAndProcced(formId, step) {
		if(document.getElementById("submitBtnTable_"+step)) {
			document.getElementById("submitBtnTable_"+step).style.display = "none";
		}
		var form_data = $("#"+formId).serialize();
		$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PreAddNewProject1.action?submit=SubmitAndProceed',
			data: form_data,
			success: function(result) {
				$("#subSubDivResult").html(result);
				document.getElementById("proDetail").className = "";
				document.getElementById("proSnapshot").className = "";
				document.getElementById("proStep1").className = "";
				document.getElementById("proStep2").className = "";
				document.getElementById("proStep3").className = "";
				document.getElementById("proStep4").className = "";
				/* document.getElementById("proStep5").className = ""; */
				document.getElementById("proStep6").className = "";
				document.getElementById("proStep7").className = "";
				document.getElementById("proStep8").className = "";
				var intStep = (parseInt(step)+1);
				 if(step == 4) {
				 	intStep = (parseInt(step)+2);
				 }
				document.getElementById("proStep"+intStep).className = "active";
	   		}
		});
	}
	
	
	function saveAndExit(formId, step) {
		if(document.getElementById("submitBtnTable_"+step)) {
			document.getElementById("submitBtnTable_"+step).style.display = "none";
		}
		var proType = document.getElementById("proType").value;
		var form_data = $("#"+formId).serialize();
		$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PreAddNewProject1.action?submit=SaveAndExit',
			data: form_data,
			success: function(result) {
				/* $("#subSubDivResult").html(result); */
				getAllProjectNameList('AllProjectNameList', proType);
			}
		});
	}
	
	
</script>

	<%
List<String> alEmpId = (List<String>)request.getAttribute("alEmpId");

List<String> alExistEmpId = (List<String>) request.getAttribute("alExistEmpId");

Map<String, String> hmEmpNames = (Map<String, String>)request.getAttribute("hmEmpNames");
Map<String, String> hmEmpLevel = (Map<String, String>)request.getAttribute("hmEmpLevel");
Map<String, String> hmLevel = (Map<String, String>)request.getAttribute("hmLevel");
Map<String, Map<String, String>>  hmWLocation = (Map<String, Map<String, String>> )request.getAttribute("hmWLocation");
Map<String, Map<String, String>> hmEmpWLocation = (Map<String, Map<String, String>>)request.getAttribute("hmEmpWLocation");

Map<String, String> empMp = (Map<String,String>)request.getAttribute("empMp");

Map<String, String> mp1 = (Map<String, String>)request.getAttribute("mp1");
Map<String, String> hmEmpCostAndRate = (Map<String, String>)request.getAttribute("hmEmpCostAndRate");

Map<String, Map<String, String>> hmLeaves = (Map<String, Map<String, String>>)request.getAttribute("hmLeaves");
Map<String, String> hmEmpSkills = (Map<String, String>)request.getAttribute("hmEmpSkills");
Map<String, String> hmEmpSkillsRates = (Map<String, String>)request.getAttribute("hmEmpSkillsRates");
Map<String, String> hmTaskAllocation = (Map<String, String>)request.getAttribute("hmTaskAllocation");

Map<String, String> hmTLMembEmp = (Map<String, String>) request.getAttribute("hmTLMembEmp");
String strActualBillingType = (String) request.getAttribute("strActualBillingType");
%>

	<s:form id="frmAddProject_2" cssClass="formcss" action="PreAddNewProject1" name="frmAddProject_2" method="post" enctype="multipart/form-data" theme="simple">  <!-- onsubmit="return checkSelect(this)" -->
		<div class="col-lg-12 col-md-12 col-sm-12" style="overflow-x: auto;">
			<s:hidden name="step" value="2"></s:hidden>
			<s:hidden name="pro_id" id="pro_id"></s:hidden>
			<s:hidden name="operation"></s:hidden>
			<s:hidden name="pageType"></s:hidden>
			<s:hidden name="proType" id="proType"></s:hidden>
			<% if((proType != null && proType.equalsIgnoreCase("P")) || ((proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) && userType != null && !userType.equals(IConstants.MANAGER))) { %>
				<div>
					<div><span> Resource Requirement</span>
					<% if(userType!=null && !userType.equals(IConstants.RECRUITER)) { %>
						<span><a  href="javascript:void(0)" style="float: right;" onclick="addNewSkill();"><i class="fa fa-plus"></i></a> </span>
					<% } %>
					</div>
					<% List<List<String>> alResReqData = (List<List<String>>) request.getAttribute("alResReqData"); %>
					<table id="tblResourceRequirement" border="0" class="table table_no_border" >
						<tr>
							<th>Work Location</th>
							<th>Skill</th>
							<th>Experience Yrs</th>
							<th>Required</th>
							<th>Gap</th>
							<% if(userType!=null && !userType.equals(IConstants.RECRUITER)) { %>
								<th>Action</th>
							<% } %>
						</tr>
						<% if(alResReqData != null && alResReqData.size()>0) { 
							for(int i=0; i<alResReqData.size(); i++) {
								List<String> innerList = alResReqData.get(i);
								//System.out.println("ANP1.jsp/1125--Work Location="+innerList.get(0));
						%>
							<% if(userType!=null && userType.equals(IConstants.RECRUITER)) { %>
								<tr id="skillTR<%=(i+1) %>">
									<td><input type="hidden" name="skillTRId" id="skillTRId<%=i %>" value="<%=i %>">
										<input type="hidden" name="proResourceReqId" id="proResourceReqId<%=i %>" value="<%=innerList.get(7) %>">
										<%=uF.showData(innerList.get(9), "All") %><br/>
										<select name="strWLocationFilter<%=i %>" id="strWLocationFilter<%=i %>" style="width: 160px !important;" multiple="multiple" >
										<%=innerList.get(0) %>
										
										</select>
									</td>
									<script type="text/javascript">
										$("#strWLocationFilter<%=i %>").multiselect().multiselectfilter();
									</script>
									<td><%=innerList.get(8) %></td>
									<td><div> <%=innerList.get(10) %> - <%=innerList.get(11) %> yrs</div> 
										<input type="text" name="reqMinExpFilter" id="reqMinExpFilter<%=innerList.get(7) %>" class="validateRequired" placeholder="min yrs" style="width: 50px !important;" value="<%=innerList.get(2) %>" onkeypress="return isNumberKey(event);" /> - 
										<input type="text" name="reqMaxExpFilter" id="reqMaxExpFilter<%=innerList.get(7) %>" class="validateRequired" placeholder="max yrs" style="width: 50px !important;" value="<%=innerList.get(3) %>" onkeypress="return isNumberKey(event);" />
									</td>
									<td><%=innerList.get(4) %></td>
									<td><input type="hidden" name="hideReqResourceGap" id="hideReqResourceGap<%=innerList.get(7) %>" value="<%=innerList.get(5) %>">
										<span id="spanReqResourceGap<%=innerList.get(7) %>" ><%=uF.parseToInt(innerList.get(5))>0 ? innerList.get(5) : "0" %></span>
									</td>
								</tr>
							<% } else { %>
								<tr id="skillTR<%=(i+1) %>">
									<td><input type="hidden" name="skillTRId" id="skillTRId<%=i %>" value="<%=i %>">
										<input type="hidden" name="proResourceReqId" id="proResourceReqId<%=i %>" value="<%=innerList.get(7) %>">
										<!-- <input type="text" name="jobProfile" id="jobProfile" class="validateRequired"/> -->
										<select name="strWLocation<%=i %>" id="strWLocation<%=i %>" style="width: 160px !important;" multiple="multiple" >
											<%=innerList.get(0) %>
											
										</select>
									</td>
									<script type="text/javascript">
										$("#strWLocation<%=i %>").multiselect().multiselectfilter();
									</script>
									<td>
										<select name="requiredSkill" id="requiredSkill<%=innerList.get(7) %>" class="validateRequired" style="width: 160px !important;">
											<option value="">Select Skill</option>
											<%=innerList.get(1) %>
										</select>
									</td>
									<td>
										<input type="text" name="reqMinExp" id="reqMinExp<%=innerList.get(7) %>" class="validateRequired" placeholder="min yrs" style="width: 50px !important;" value="<%=innerList.get(2) %>" onkeypress="return isNumberKey(event);" /> - 
										<input type="text" name="reqMaxExp" id="reqMaxExp<%=innerList.get(7) %>" class="validateRequired" placeholder="max yrs" style="width: 50px !important;" value="<%=innerList.get(3) %>" onkeypress="return isNumberKey(event);" />
									</td>
									<td>
										<input type="text" name="reqResource" id="reqResource<%=innerList.get(7) %>" class="validateRequired" style="width: 50px !important;" value="<%=innerList.get(4) %>" onkeypress="return isNumberKey(event);" onkeyup="setReqResToGap('<%=innerList.get(7) %>');"/>
									</td>
									<td><input type="hidden" name="hideReqResourceGap" id="hideReqResourceGap<%=innerList.get(7) %>" value="<%=innerList.get(5) %>">
										<input type="text" name="reqResourceGap" id="reqResourceGap<%=innerList.get(7) %>" style="width: 50px !important;" value="<%=innerList.get(5) %>" readonly="readonly"/>
									</td>
									<td><a href="javascript:void(0);" onclick="deleteSkill('<%=(i+1) %>','<%=innerList.get(7) %>')"><i class="fa fa-times"></i></a></td>
								</tr>
								<% } %>
							<% } %>
						<% } else { %>
							<tr id="skillTR0">
								<td><input type="hidden" name="skillTRId" id="skillTRId0" value="0">
									<input type="hidden" name="proResourceReqId" id="proResourceReqId0" value="0">
									<!-- <input type="text" name="jobProfile" id="jobProfile" class="validateRequired"/> -->
							<!-- ===start parvez date: 07-12-2021=== -->		
									<%-- <select name="strWLocation" id="strWLocation0" style="width: 160px !important;" multiple="multiple" ><%=(String) request.getAttribute("sbWLocs") %></select> --%>
									<select name="strWLocation0" id="strWLocation0" style="width: 160px !important;" multiple="multiple" ><%=(String) request.getAttribute("sbWLocs") %></select>
							<!-- ===end parvez date: 07-12-2021=== -->
							
								</td>
								<td>
									<select name="requiredSkill" id="requiredSkill0" class="validateRequired" style="width: 160px !important;">
										<option value="">Select Skill</option>
										<%=(String) request.getAttribute("sbSkills") %>
									</select>
									
								</td>
								<td>
									<input type="text" name="reqMinExp" id="reqMinExp0" class="validateRequired" placeholder="min yrs" style="width: 50px !important;" onkeypress="return isNumberKey(event);" /> - 
									<input type="text" name="reqMaxExp" id="reqMaxExp0" class="validateRequired" placeholder="max yrs" style="width: 50px !important;" onkeypress="return isNumberKey(event);" />
								</td>
								<td>
									<input type="text" name="reqResource" id="reqResource0" class="validateRequired" style="width: 50px !important;" onkeypress="return isNumberKey(event);" onkeyup="setReqResToGap('0');"/>
								</td>
								<td>
									<input type="text" name="reqResourceGap" id="reqResourceGap0" style="width: 50px !important;" readonly="readonly"/>
								</td>
								<td></td>
							</tr>
						<% } %>	
					</table>
					<div id="createResourceRequirement_2" style="text-align: center;">
						<input type="hidden" name="skillCount" id="skillCount" value="<%=alResReqData !=null ? (alResReqData.size()+1) : "0" %>" />
						<% if(userType!=null && userType.equals(IConstants.RECRUITER)) { %>
							<input type="submit" name="submit" id="btnOk" value="Search Resource" class="btn btn-primary cstm-validate" onclick="createResourceRequirement('frmAddProject_2', '2');" />
						<% } else { %>
							<%=uF.showData((String)request.getAttribute(IConstants.MESSAGE), "") %>
							<input type="submit" name="submit" id="btnOk" value="Create Resource Request" class="btn btn-primary cstm-validate" onclick="createResourceRequirement('frmAddProject_2', '2');" />
						<% } %>
					</div>
				</div>
			<% } %>
			</s:form>
			
			<table border="0" class="table table_no_border" style="border-bottom: 1px solid #f4f4f4;">
				<%-- <%if((proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) && userType != null && !userType.equals(IConstants.MANAGER)) { %>
				<tr>
					<td colspan="5" class="txtlabel" valign="top">Select Team:<sup>*</sup></td>
				</tr>
				<tr>	
					<td id="wLocationListID">
						<p align="center">Location</p> <s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" 
							cssStyle="width: 160px !important;" listValue="wLocationName" multiple="true" list="workLocationList" /></td>

					<td id="levelListID">
						<p align="center">Level</p> <s:select theme="simple" name="f_level" id="f_level" listKey="levelId"
							listValue="levelCodeName" cssStyle="width: 160px !important;" multiple="true" list="levelList" /></td>

					<td id="departmentListID">
						<p align="center">Department</p> <s:select theme="simple" name="f_department" id="f_department" listKey="deptId"
							listValue="deptName" cssStyle="width: 160px !important;" multiple="true" list="departmentList" /></td>

					<td id="skillListID">
						<p align="center">Skills</p> <s:select theme="simple" label="Select Skill" name="skill" id="skill" listKey="skillsId"
							cssStyle="width: 160px !important;" listValue="skillsName" multiple="true" list="skillList" /></td>

					<td><p align="center">&nbsp;</p> <input type="button" name="btnSearch" class="btn btn-primary" value="Search" onclick="getTeam();" /></td>
				</tr>
				<% } else { %>
				<tr>
					<td style="display: none;">
						<s:hidden name="f_strWLocation" id="f_strWLocation"></s:hidden>
						<s:hidden name="f_level" id="f_level"></s:hidden>
						<s:hidden name="f_department" id="f_department"></s:hidden>
						<s:hidden name="skill" id="skill"></s:hidden>
					</td>
				</tr>
				<% } %> --%>
				<tr>
					<td colspan="5" nowrap="nowrap" class="txtlabel" valign="top">Selected Team:<sup>*</sup></td>
				</tr>
				<tr>
					<td colspan="5">
						<div id="teamLeadListID">
						<div id="the_div"><div id="ajaxLoadImage"></div></div>
						</div> <!-- <iframe id="teamLeadListID" target='_parent' style="height: 950px; width: 1070px;" scrolling="no" frameborder="0"></iframe> -->
						<!-- onload='javascript:resizeIframe(this);' --></td>
				</tr>
			</table>
		
		
			<div style="width: 50%; float: left;">
				<div style="width: 100%; float: left; margin: 3px">
					<div style="width: 20px; height: 100%; text-align: center; background-color: #00CC00; float: left;">&nbsp;</div>
					<div style="font-size: 10px; float: left; padding-left: 5px;">No Leaves</div>
				</div>
				<div style="width: 100%; float: left; margin: 3px">
					<div style="width: 20px; height: 100%; text-align: center; background-color: #99FF00; float: left">&nbsp;</div>
					<div style="font-size: 10px; float: left; padding-left: 5px;">Leaves less than 4</div>
				</div>
				<div style="width: 100%; float: left; margin: 3px">
					<div style="width: 20px; height: 100%; text-align: center; background-color: #FFFF33; float: left">&nbsp;</div>
					<div style="font-size: 10px; float: left; padding-left: 5px;">Leaves between 4 - 6</div>
				</div>
				<div style="width: 100%; float: left; margin: 3px">
					<div style="width: 20px; height: 100%; text-align: center; background-color: #FF9900; float: left">&nbsp;</div>
					<div style="font-size: 10px; float: left; padding-left: 5px;">Leaves between 7 - 14</div>
				</div>
				<div style="width: 100%; float: left; margin: 3px">
					<div style="width: 20px; height: 100%; text-align: center; background-color: #FF3300; float: left">&nbsp;</div>
					<div style="font-size: 10px; float: left; padding-left: 5px;">Leaves more than 14</div>
				</div>
			</div>

			<div style="width: 50%; float: left;">
				<div style="width: 100%; float: left; margin: 3px">
					<div style="width: 20px; height: 100%; text-align: center; background-color: red; float: left">&nbsp;</div>
					<div style="font-size: 10px; float: left; padding-left: 5px;">Tasks allocated more than 5</div>
				</div>
				<div style="width: 100%; float: left; margin: 3px">
					<div style="width: 20px; height: 100%; text-align: center; background-color: yellow; float: left">&nbsp;</div>
					<div style="font-size: 10px; float: left; padding-left: 5px;">Tasks allocated between 2 - 5</div>
				</div>
				<div style="width: 100%; float: left; margin: 3px">
					<div style="width: 20px; height: 100%; text-align: center; background-color: lightgreen; float: left">&nbsp;</div>
					<div style="font-size: 10px; float: left; padding-left: 5px;">Tasks allocated less than 2</div>
				</div>
			</div>

		</div>
		<div class="clr"></div>

	

<script>
	
		$(".cstm-validate").click(function(){
			$('.validateRequired').filter(':hidden').prop('required',false);
			$('.validateRequired').filter(':visible').prop('required',true);
		});
    
	</script>
	
</s:if>

<s:if test="step==3">
	<script>

	var proStDate = '<%=request.getAttribute("PROJECT_START_DATE_MM_DD")%>';
	var proEdDate = '<%=request.getAttribute("PROJECT_END_DATE_MM_DD")%>';
	
	$(function () {
		
		
		$("#deadline10").datepicker({
			format: 'dd/mm/yyyy',
			startDate : new Date(proStDate),
			endDate : new Date(proEdDate),
			autoclose: true
		}).on('changeDate', function (selected) {
	        var minDate = new Date(selected.date.valueOf());
	        $('#startDate0').datepicker('setEndDate', minDate);
		});
		
		
		$("#startDate0").datepicker({
			format: 'dd/mm/yyyy',
			startDate : new Date(proStDate),
			endDate : new Date(proEdDate),
			autoclose: true
		}).on('changeDate', function (selected) {
	        var minDate = new Date(selected.date.valueOf());
	        $('#deadline10').datepicker('setStartDate', minDate);
		});
		
		
		var taskAndSubtaskCount = '<%=request.getAttribute("taskAndSubtaskCount")%>';
		for(var i=0; i<taskAndSubtaskCount; i++) {
			if(document.getElementById("startDate"+i) && document.getElementById("deadline1"+i)) {
				setDate(i);
			}
			if(document.getElementById("substartDate"+i) && document.getElementById("subdeadline1"+i)) {
				setSubDate(i);
			}
		}
		
	});

	var proStartDate = '<%=request.getAttribute("PROJECT_START_DATE_C")%>';
	var proEndDate = '<%=request.getAttribute("PROJECT_END_DATE_C")%>';
	
	var opt2 = '<%=request.getAttribute("sb2")%>';
	var opt3 = '<%=request.getAttribute("sb3")%>'; 
	var subopt2 = '<%=request.getAttribute("subsb2")%>';
	var subopt3 = '<%=request.getAttribute("subsb3")%>';


	<% Map hmProjectDocuments = (Map)request.getAttribute("hmProjectDocuments");
		if(hmProjectDocuments==null)hmProjectDocuments=new HashMap();
	%>

	function saveTaskAndGetTaskId(cnt) {
		var proId = document.getElementById("pro_id1").value;
		var taskName = document.getElementById("taskname"+cnt).value;
		var taskID = document.getElementById("taskID"+cnt).value;
		//alert("proId ===>> " + proId);
		getContent('addTaskSpan'+cnt, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+'&taskId='+taskID+'&taskName='+encodeURIComponent(taskName)+"&count="+cnt+'&type=Task');
	}


	function saveSubTaskAndGetSubTaskId(cnt, taskTRId) {
		var proId = document.getElementById("pro_id1").value;
		var taskId = document.getElementById("taskID"+taskTRId).value;
		var subTaskName = document.getElementById("subtaskname"+cnt).value;
		var subTaskID = document.getElementById("subTaskID"+cnt).value;
		if(parseFloat(taskId) > 0) {
			getContent('addSubTaskSpan'+cnt, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+'&subTaskId='+subTaskID+'&subTaskName='+encodeURIComponent(subTaskName)+'&taskTRId='+taskTRId+'&taskId='+taskId+"&count="+cnt+'&type=SubTask');
		} else {
			alert('No task available for this sub task, Please add task.');
		}
	}


	function deleteTaskFromDB(taskTRId) {
		var proId = document.getElementById("pro_id1").value;
		var taskId = document.getElementById("taskID"+taskTRId).value;
		getContent('addTaskSpan'+taskTRId, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+'&taskId='+taskId+'&type=DelTask');
	}
	
	
	function deleteSubTaskFromDB(subtaskTRId) {
		var proId = document.getElementById("pro_id1").value;
		var subTaskId = document.getElementById("subTaskID"+subtaskTRId).value;
		getContent('addSubTaskSpan'+subtaskTRId, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+'&taskId='+subTaskId+'&type=DelTask');
	}
	
	
	function getSkillwiseEmployee(skillId, cnt) {
		var proId = document.getElementById("pro_id1").value;
		getContent('empSpan'+cnt, 'GetSkillwiseEmployee.action?proId='+proId+'&skillId='+skillId+'&count='+cnt+'&type=Task');
	}
	
	
	function getSubSkillwiseEmployee(skillId, cnt, taskTRId) {
		var proId = document.getElementById("pro_id1").value;
		getContent('subEmpSpan'+cnt, 'GetSkillwiseEmployee.action?proId='+proId+'&skillId='+skillId+'&taskTRId='+taskTRId+'&count='+cnt+'&type=SubTask');
	}
	
	
	function repeatTask(tCnt, isRecurr) {
		var taskCnt = document.getElementById("taskcount").value;
		var cnt=(parseInt(taskCnt)+1);
	    var table = document.getElementById("taskTable");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    
	    var recurrChechbox = "";
		if(isRecurr == 'Y') {
			recurrChechbox = "<input type=\"checkbox\" name=\"recurringTask\" id=\"recurringTask"+cnt+"\" onclick=\"setValue('isRecurringTask"+cnt+"');\" title=\"Add task to recurring in next frequency\"/>Recurr Task";
		}
	    row.id="task_TR"+cnt;
	    var cell0 = row.insertCell(0);
	    cell0.innerHTML = "<input type=\"hidden\" name=\"taskTRId\" id=\"taskTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"hidden\" name=\"taskDescription\" id=\"taskDescription"+cnt+"\">"+
	    "<textarea name=\"taskname\" id=\"taskname"+cnt+"\" class=\"validateRequired\" style=\"width:160px !important;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"')\"> </textarea>"+
	    /* "<input type=\"text\" name=\"taskname\" id=\"taskname"+cnt+"\" class=\"validateRequired\" style=\"width:160px !important;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"')\">"+ */
	    "<span id=\"addTaskSpan"+cnt+"\"><input type=\"hidden\" name=\"taskID\" id=\"taskID"+cnt+"\" value=\"\"></span>"+
	    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+cnt+"', 'taskDescription', 'T');\">D</a>"+
	    "&nbsp;"+recurrChechbox+
	    "<input type=\"hidden\" name=\"isRecurringTask\" id=\"isRecurringTask"+cnt+"\" value=\"0\"/></div>";
	    
	    var cell1 = row.insertCell(1);
	    cell1.innerHTML = "<span id=\"dependencySpan"+cnt+"\"> <select name=\"dependency\" id=\"dependency"+cnt+"\" style=\"width:135px !important;\" ><option value=\"\">Select Dependency</option></select></span>";
		
	    var cell2 = row.insertCell(2);
	    cell2.innerHTML = "<select name=\"dependencyType\" id=\"dependencyType"+cnt+"\" style=\"width:135px !important;\" onchange=\"setDependencyPeriod(this.value, '"+cnt+"', 'Task');\"><option value=\"\">Select Dependency Type</option>"
		    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
		
		var cell3 = row.insertCell(3);
		cell3.innerHTML = opt3;
		
		var cell4 = row.insertCell(4);
		cell4.innerHTML = "<span id=\"empSpan"+cnt+"\">"+opt2+"</span>";
		
		var cell5 = row.insertCell(5);
		cell5.innerHTML = "<input type=\"text\" id=\"startDate"+cnt+"\" name=\"startDate\" class=\"validateRequired\" style=\"width:90px !important;\">";

		var cell6 = row.insertCell(6);
		cell6.innerHTML = "<input type=\"text\" id=\"deadline1"+cnt+"\" class=\"validateRequired\" name=\"deadline1\" style=\"width:90px !important;\">";
		
		var cell7 = row.insertCell(7);
		cell7.innerHTML = "<input type=\"text\" id=\"idealTime"+cnt+"\" name=\"idealTime\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:50px !important; text-align:right;\">";
		
		var cell8 = row.insertCell(8);
		cell8.innerHTML = "<input type=\"checkbox\" name=\"billableTask\" id=\"billableTask"+cnt+"\" onclick=\"setValue('isBillableTask"+cnt+"');\" title=\"Please tick for billable\" />"+
			"<input type=\"hidden\" name=\"isBillableTask\" id=\"isBillableTask"+cnt+"\" value=\"0\" />";
		
		var cell9 = row.insertCell(9);
		cell9.innerHTML = "<input type=\"text\" name=\"colourCode\" id=\"colourCode"+cnt+"\" class=\"validateRequired\" style=\"width:10px !important;\" readonly=\"readonly\"/>";
					
		var cell11 = row.insertCell(10);
		cell11.setAttribute("nowrap","nowrap");
		cell11.setAttribute("valign","top");
		cell11.innerHTML = "<select name=\"taskActions"+cnt+"\" id=\"taskActions"+cnt+"\" style=\"width: 100px !important;\" onchange=\"executeTaskActions(this.value, this.parentNode.parentNode.rowIndex, '"+cnt+"', '', '', '"+isRecurr+"');\">"+
		"<option value=\"\">Actions</option><option value=\"1\">Delete</option><option value=\"3\">Repeat Task </option><option value=\"4\">Add Sub-task </option>"+
		"</select>";
		
	    document.getElementById("taskcount").value = cnt;
	    
	    document.getElementById("emp_id").name = "emp_id"+cnt;
	    document.getElementById("emp_id").id = "emp_id"+cnt;
	    document.getElementById("priority").id = "priority"+cnt;
	    $("#emp_id"+cnt).multiselect().multiselectfilter();
	    
	    document.getElementById('taskname'+cnt).value = document.getElementById('taskname'+tCnt).value;
	    document.getElementById('taskDescription'+cnt).value = document.getElementById('taskDescription'+tCnt).value;
	    

	    getTasksForDependency(cnt);
	    document.getElementById('dependency'+cnt).selectedIndex = document.getElementById('dependency'+tCnt).selectedIndex;
	    document.getElementById('dependencyType'+cnt).selectedIndex = document.getElementById('dependencyType'+tCnt).selectedIndex;
	    document.getElementById('priority'+cnt).selectedIndex = document.getElementById('priority'+tCnt).selectedIndex;

    	document.getElementById('emp_id'+cnt).selectedIndex = document.getElementById('emp_id'+tCnt).selectedIndex;

    	document.getElementById('startDate'+cnt).value = document.getElementById('startDate'+tCnt).value;
    	document.getElementById('deadline1'+cnt).value = document.getElementById('deadline1'+tCnt).value;

    	document.getElementById('idealTime'+cnt).value = document.getElementById('idealTime'+tCnt).value;
    	
    	document.getElementById('colourCode'+cnt).value = document.getElementById('colourCode'+tCnt).value;
    	document.getElementById('colourCode'+cnt).style.backgroundColor = document.getElementById('colourCode'+tCnt).value;
	    
	    setDate(cnt);
	   
	}
	

	function addNewTask(isRecurr) {
		var taskCnt = document.getElementById("taskcount").value;
		var cnt=(parseInt(taskCnt)+1);
	    var table = document.getElementById("taskTable");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(rowCount);
	    var myColor = '#'+(Math.random()*0xFFFFFF<<0).toString(16);
	    
	    var recurrChechbox = "";
		if(isRecurr == 'Y') {
			recurrChechbox = "<input type=\"checkbox\" name=\"recurringTask\" id=\"recurringTask"+cnt+"\" onclick=\"setValue('isRecurringTask"+cnt+"');\" title=\"Add task to recurring in next frequency\"/>Recurr Task";
		}
	    row.id="task_TR"+cnt;
	    /* "+proStartDate+" "+proEndDate+" */
	    var cell0 = row.insertCell(0);
	    cell0.innerHTML = "<input type=\"hidden\" name=\"taskTRId\" id=\"taskTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"hidden\" name=\"taskDescription\" id=\"taskDescription"+cnt+"\">"+
	    "<textarea name=\"taskname\" id=\"taskname"+cnt+"\" class=\"validateRequired\" style=\"width:160px !important;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"')\"> </textarea>"+
	    /* "<input type=\"text\" name=\"taskname\" id=\"taskname"+cnt+"\" class=\"validateRequired\" style=\"width:160px !important;\" onchange=\"saveTaskAndGetTaskId('"+cnt+"')\">"+ */
	    "<span id=\"addTaskSpan"+cnt+"\"><input type=\"hidden\" name=\"taskID\" id=\"taskID"+cnt+"\" value=\"\"></span>"+
	    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+cnt+"', 'taskDescription', 'T');\">D</a>"+
	    "&nbsp;"+recurrChechbox+
	    "<input type=\"hidden\" name=\"isRecurringTask\" id=\"isRecurringTask"+cnt+"\" value=\"0\"/></div>";
	    
	    var cell1 = row.insertCell(1);
	    cell1.innerHTML = "<span id=\"dependencySpan"+cnt+"\"> <select name=\"dependency\" id=\"dependency"+cnt+"\" style=\"width:135px !important;\" ><option value=\"\">Select Dependency</option></select></span>";
		
	    var cell2 = row.insertCell(2);
	    cell2.innerHTML = "<select name=\"dependencyType\" id=\"dependencyType"+cnt+"\" style=\"width:135px !important;\" onchange=\"setDependencyPeriod(this.value, '"+cnt+"', 'Task');\"><option value=\"\">Select Dependency Type</option>"
		    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
		
		var cell3 = row.insertCell(3);
		cell3.innerHTML = opt3;
		
		var cell4 = row.insertCell(4);
		cell4.innerHTML = "<span id=\"empSpan"+cnt+"\">"+opt2+"</span>";		
		
		var cell5 = row.insertCell(5);
		cell5.innerHTML = "<input type=\"text\" id=\"startDate"+cnt+"\" name=\"startDate\" value=\"\" class=\"validateRequired\" style=\"width:90px !important;\">";

		var cell6 = row.insertCell(6);
		cell6.innerHTML = "<input type=\"text\" id=\"deadline1"+cnt+"\" class=\"validateRequired\" name=\"deadline1\" value=\"\" style=\"width:90px !important;\">";
		
		var cell7 = row.insertCell(7);
		cell7.innerHTML = "<input type=\"text\" id=\"idealTime"+cnt+"\" name=\"idealTime\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:50px !important;text-align:right;\">";
		
		var cell8 = row.insertCell(8);
		cell8.innerHTML = "<input type=\"checkbox\" name=\"billableTask\" id=\"billableTask"+cnt+"\" onclick=\"setValue('isBillableTask"+cnt+"');\" title=\"Please tick for billable\" />"+
			"<input type=\"hidden\" name=\"isBillableTask\" id=\"isBillableTask"+cnt+"\" value=\"0\" />";
			
		var cell9 = row.insertCell(9);
		cell9.innerHTML = "<input type=\"text\" name=\"colourCode\" id=\"colourCode"+cnt+"\" class=\"validateRequired\" style=\"width:10px !important; background-color: "+myColor+"\" value=\""+myColor+"\" readonly=\"readonly\"/>";
					
		var cell11 = row.insertCell(10);
		cell11.setAttribute("nowrap","nowrap");
		cell11.setAttribute("valign","top");
		cell11.innerHTML = "<select name=\"taskActions"+cnt+"\" id=\"taskActions"+cnt+"\" style=\"width: 100px !important;\" onchange=\"executeTaskActions(this.value, this.parentNode.parentNode.rowIndex, '"+cnt+"', '', '', '"+isRecurr+"');\">"+
		"<option value=\"\">Actions</option><option value=\"1\">Delete</option><option value=\"3\">Repeat Task </option><option value=\"4\">Add Sub-task </option>"+
		"</select>";
				
	    document.getElementById("taskcount").value = cnt;
	    
	    document.getElementById("emp_id").name = "emp_id"+cnt;
	    document.getElementById("emp_id").id = "emp_id"+cnt;
	    $("#emp_id"+cnt).multiselect().multiselectfilter();
	    
	    document.getElementById("priority").id = "priority"+cnt;
	    
	    getTasksForDependency(cnt);
	    
	    setDate(cnt);
	}
	
	
	function getTasksForDependency(cnt) {
		var proId = document.getElementById("pro_id1").value;
		getContent('dependencySpan'+cnt, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+"&count="+cnt+'&type=GetTasks');
	}

	
	function deleteTask(count) {
		 if(document.getElementById("TskTRId"+count)) {
			alert("Please first delete sub task of this task then delete this task.");
		} else {
			if(confirm('Are you sure, you want to delete this task?')) {
				deleteTaskFromDB(count);
				var trIndex = document.getElementById("task_TR"+count).rowIndex;
			    document.getElementById("taskTable").deleteRow(trIndex);
			}
		}
	}
	

	function repeatSubTask(stCnt, taskTRId, rwIndex, isRecurr) {
	
		var taskCnt = document.getElementById("taskcount").value;
		var cnt=(parseInt(taskCnt)+1);
		var val=(parseInt(rwIndex)+1);
	    var table = document.getElementById("taskTable");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(val);
	    
	    var recurrChechbox = "";
		if(isRecurr == 'Y') {
			recurrChechbox = "<input type=\"checkbox\" name=\"recurringSubTask"+taskTRId+"\" id=\"recurringSubTask"+cnt+"\" onclick=\"setValue('isRecurringSubTask"+cnt+"');\" title=\"Add sub task to recurring in next frequency\"/>Recurr Subtask";
		}
		
	    row.id="task_TR"+cnt;
	    var cell0 = row.insertCell(0);
	    cell0.setAttribute('style', 'text-align: right;');
	    cell0.innerHTML = "<input type=\"hidden\" name=\"TskTRId"+taskTRId+"\" id=\"TskTRId"+taskTRId+"\" value=\""+taskTRId+"\"><input type=\"hidden\" name=\"subTaskTRId"+taskTRId+"\" id=\"subTaskTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"hidden\" name=\"subTaskDescription"+taskTRId+"\" id=\"subTaskDescription"+cnt+"\">"+
	    "<textarea name=\"subtaskname"+taskTRId+"\" id=\"subtaskname"+cnt+"\" class=\"validateRequired\" style=\"width:120px !important;\" onchange=\"saveSubTaskAndGetSubTaskId('"+cnt+"', '"+taskTRId+"')\"> </textarea>"+
	    /* "<input type=\"text\" name=\"subtaskname"+taskTRId+"\" id=\"subtaskname"+cnt+"\" class=\"validateRequired\" style=\"width:120px !important;\" onchange=\"saveSubTaskAndGetSubTaskId('"+cnt+"', '"+taskTRId+"')\">"+ */
	    "<span id=\"addSubTaskSpan"+cnt+"\"><input type=\"hidden\" name=\"subTaskID"+taskTRId+"\" id=\"subTaskID"+cnt+"\" value=\"\"></span>"+
	    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+cnt+"', 'subTaskDescription', 'ST');\">D</a>"+
	    "&nbsp;"+recurrChechbox+
	    "<input type=\"hidden\" name=\"isRecurringSubTask"+taskTRId+"\" id=\"isRecurringSubTask"+cnt+"\" value=\"0\"/></div>";
	    
	    var cell1 = row.insertCell(1);
	    cell1.innerHTML = "<span id=\"subDependencySpan"+cnt+"\"><select name=\"subDependency"+taskTRId+"\" id=\"subDependency"+cnt+"\" style=\"width:135px !important;\"><option value=\"\">Select Dependency</option></select></span>";
		
	    var cell2 = row.insertCell(2);
	    cell2.innerHTML = "<select name=\"subDependencyType"+taskTRId+"\" id=\"subDependencyType"+cnt+"\" style=\"width:135px !important;\" onchange=\"setDependencyPeriod(this.value, '"+cnt+"', 'SubTask');\"><option value=\"\">Select Dependency Type</option>"
		    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
		
		var cell3 = row.insertCell(3);
		cell3.innerHTML = subopt3;
		
		var cell4 = row.insertCell(4);
		cell4.innerHTML = "<span id=\"subEmpSpan"+cnt+"\">"+subopt2+"</span>";
		
		var cell5 = row.insertCell(5);
		cell5.innerHTML = "<input type=\"text\" id=\"substartDate"+cnt+"\" name=\"substartDate"+taskTRId+"\" class=\"validateRequired\" style=\"width:90px !important;\">";

		var cell6 = row.insertCell(6);
		cell6.innerHTML = "<input type=\"text\" id=\"subdeadline1"+cnt+"\" class=\"validateRequired\" name=\"subdeadline1"+taskTRId+"\" style=\"width:90px !important;\">";
		
		var cell7 = row.insertCell(7);
		cell7.innerHTML = "<input type=\"text\" id=\"subidealTime"+cnt+"\" name=\"subidealTime"+taskTRId+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:50px !important; text-align:right;\">";
		
		var cell8 = row.insertCell(8);
		cell8.innerHTML = "<input type=\"checkbox\" name=\"billableSubTask"+taskTRId+"\" id=\"billableSubTask"+cnt+"\" onclick=\"setValue('isBillableSubTask"+cnt+"');\" title=\"Please tick for billable\" />"+
			"<input type=\"hidden\" name=\"isBillableSubTask"+taskTRId+"\" id=\"isBillableSubTask"+cnt+"\" value=\"0\" />";
			
		var cell9 = row.insertCell(9);
		cell9.innerHTML = "<input type=\"text\" name=\"subcolourCode"+taskTRId+"\" id=\"subcolourCode"+cnt+"\" class=\"validateRequired\" style=\"width:10px !important;\" readonly=\"readonly\"/>";
					
		var cell11 = row.insertCell(10);
		cell11.setAttribute("nowrap","nowrap");
		cell11.setAttribute("valign","top");
		cell11.innerHTML = "<select name=\"subtaskActions"+cnt+"\" id=\"subtaskActions"+cnt+"\" style=\"width: 100px !important;\" onchange=\"executeSubTaskActions(this.value, this.parentNode.parentNode.rowIndex, '"+cnt+"', '', '"+taskTRId+"', '', '"+isRecurr+"');\">"+
		"<option value=\"\">Actions</option><option value=\"1\">Delete</option><option value=\"3\">Repeat Sub-task </option><option value=\"4\">Add Sub-task </option>"+
		"</select>";
		
	    
		document.getElementById("taskcount").value = cnt;
	    
	    document.getElementById("sub_emp_id").name = "sub_emp_id"+taskTRId+"_"+cnt;
	    document.getElementById("sub_emp_id").id = "sub_emp_id"+cnt;
	    $("#sub_emp_id"+cnt).multiselect().multiselectfilter();
	    
	    document.getElementById("subpriority").name = "subpriority"+taskTRId;
	    document.getElementById("subpriority").id = "subpriority"+cnt;
	   
	    document.getElementById('subtaskname'+cnt).value = document.getElementById('subtaskname'+stCnt).value;
	    document.getElementById('subTaskDescription'+cnt).value = document.getElementById('subTaskDescription'+stCnt).value;
	    
	    getSubTasksForDependency(cnt, taskTRId);
	    document.getElementById('subDependency'+cnt).selectedIndex = document.getElementById('subDependency'+stCnt).selectedIndex;
	    document.getElementById('subDependencyType'+cnt).selectedIndex = document.getElementById('subDependencyType'+stCnt).selectedIndex;
	    document.getElementById('subpriority'+cnt).selectedIndex = document.getElementById('subpriority'+stCnt).selectedIndex;
	    
    	document.getElementById('sub_emp_id'+cnt).selectedIndex = document.getElementById('sub_emp_id'+stCnt).selectedIndex;
    	
    	document.getElementById('substartDate'+cnt).value = document.getElementById('substartDate'+stCnt).value;
    	document.getElementById('subdeadline1'+cnt).value = document.getElementById('subdeadline1'+stCnt).value;
    	
    	document.getElementById('subidealTime'+cnt).value = document.getElementById('subidealTime'+stCnt).value;
    	
    	document.getElementById('subcolourCode'+cnt).value = document.getElementById('subcolourCode'+stCnt).value;
    	document.getElementById('subcolourCode'+cnt).style.backgroundColor = document.getElementById('subcolourCode'+stCnt).value;
        
	    setSubDate(cnt);
	}


	function addNewSubTask(taskTRId, rwIndex, isRecurr) {
		var taskCnt = document.getElementById("taskcount").value;
		var cnt=(parseInt(taskCnt)+1);
		var val=(parseInt(rwIndex)+1);
	    var table = document.getElementById("taskTable");
	    var rowCount = table.rows.length;
	    var row = table.insertRow(val);
	    var myColor = '#'+(Math.random()*0xFFFFFF<<0).toString(16);
	    
	    var recurrChechbox = "";
		if(isRecurr == 'Y') {
			recurrChechbox = "<input type=\"checkbox\" name=\"recurringSubTask"+taskTRId+"\" id=\"recurringSubTask"+cnt+"\" onclick=\"setValue('isRecurringSubTask"+cnt+"');\" title=\"Add sub task to recurring in next frequency\"/>Recurr Subtask";
		}
		
	    row.id="task_TR"+cnt;
	    var cell0 = row.insertCell(0);
	    cell0.setAttribute('style', 'text-align: right;' );
	    cell0.innerHTML = "<input type=\"hidden\" name=\"TskTRId"+taskTRId+"\" id=\"TskTRId"+taskTRId+"\" value=\""+taskTRId+"\"><input type=\"hidden\" name=\"subTaskTRId"+taskTRId+"\" id=\"subTaskTRId"+cnt+"\" value=\""+cnt+"\">"+
	    "<input type=\"hidden\" name=\"subTaskDescription"+taskTRId+"\" id=\"subTaskDescription"+cnt+"\">"+
	    "<textarea name=\"subtaskname"+taskTRId+"\" id=\"subtaskname"+cnt+"\" class=\"validateRequired\" style=\"width:120px !important;\" onchange=\"saveSubTaskAndGetSubTaskId('"+cnt+"', '"+taskTRId+"')\"> </textarea>"+
	    /* "<input type=\"text\" name=\"subtaskname"+taskTRId+"\" id=\"subtaskname"+cnt+"\" class=\"validateRequired\" style=\"width:120px !important;\" onchange=\"saveSubTaskAndGetSubTaskId('"+cnt+"', '"+taskTRId+"')\">"+ */
	    "<span id=\"addSubTaskSpan"+cnt+"\"><input type=\"hidden\" name=\"subTaskID"+taskTRId+"\" id=\"subTaskID"+cnt+"\" value=\"\"></span>"+
	    "<div><a href=\"javascript:void(0)\" onclick=\"updateTaskDescription('"+cnt+"', 'subTaskDescription', 'ST');\">D</a>"+
	    "&nbsp;"+recurrChechbox+
	    "<input type=\"hidden\" name=\"isRecurringSubTask"+taskTRId+"\" id=\"isRecurringSubTask"+cnt+"\" value=\"0\"/></div>";
	    
	    var cell1 = row.insertCell(1);
	    cell1.innerHTML = "<span id=\"subDependencySpan"+cnt+"\"><select name=\"subDependency"+taskTRId+"\" id=\"subDependency"+cnt+"\" style=\"width:135px !important;\"><option value=\"\">Select Dependency</option></select></span>";
		
	    var cell2 = row.insertCell(2);
	    cell2.innerHTML = "<select name=\"subDependencyType"+taskTRId+"\" id=\"subDependencyType"+cnt+"\" style=\"width:135px !important;\" onchange=\"setDependencyPeriod(this.value, '"+cnt+"', 'SubTask');\"><option value=\"\">Select Dependency Type</option>"
		    +"<option value=\"0\">Start-Start</option><option value=\"1\">Finish-Start</option></select>";
		
		var cell3 = row.insertCell(3);
		cell3.innerHTML = subopt3;
		
		var cell4 = row.insertCell(4);
		cell4.innerHTML = "<span id=\"subEmpSpan"+cnt+"\">"+subopt2+"</span>";
		
		var cell5 = row.insertCell(5);
		cell5.innerHTML = "<input type=\"text\" id=\"substartDate"+cnt+"\" name=\"substartDate"+taskTRId+"\" class=\"validateRequired\" value=\"\" style=\"width:90px !important;\">";

		var cell6 = row.insertCell(6);
		cell6.innerHTML = "<input type=\"text\" id=\"subdeadline1"+cnt+"\" class=\"validateRequired\" name=\"subdeadline1"+taskTRId+"\" value=\"\" style=\"width:90px !important;\">";
		
		var cell7 = row.insertCell(7);
		cell7.innerHTML = "<input type=\"text\" id=\"subidealTime"+cnt+"\" name=\"subidealTime"+taskTRId+"\" onkeypress=\"return isNumberKey(event)\" class=\"validateRequired\" style=\"width:50px !important; text-align:right;\">";
		
		var cell8 = row.insertCell(8);
		cell8.innerHTML = "<input type=\"checkbox\" name=\"billableSubTask"+taskTRId+"\" id=\"billableSubTask"+cnt+"\" onclick=\"setValue('isBillableSubTask"+cnt+"');\" title=\"Please tick for billable\" />"+
			"<input type=\"hidden\" name=\"isBillableSubTask"+taskTRId+"\" id=\"isBillableSubTask"+cnt+"\" value=\"0\" />";
			
		var cell9 = row.insertCell(9);
		cell9.innerHTML = "<input type=\"text\" name=\"subcolourCode"+taskTRId+"\" id=\"subcolourCode"+cnt+"\" class=\"validateRequired\" style=\"width:10px !important; background-color: "+myColor+"\" value=\""+myColor+"\" readonly=\"readonly\"/>";
		
		var cell11 = row.insertCell(10);
		cell11.setAttribute("nowrap","nowrap");
		cell11.setAttribute("valign","top");
		cell11.innerHTML = "<select name=\"subtaskActions"+cnt+"\" id=\"subtaskActions"+cnt+"\" style=\"width: 100px !important;\" onchange=\"executeSubTaskActions(this.value, this.parentNode.parentNode.rowIndex, '"+cnt+"', '', '"+taskTRId+"', '', '"+isRecurr+"');\">"+
		"<option value=\"\">Actions</option><option value=\"1\">Delete</option><option value=\"3\">Repeat Sub-task </option><option value=\"4\">Add Sub-task </option>"+
		"</select>";
		
		document.getElementById("taskcount").value = cnt;
	    
	    document.getElementById("sub_emp_id").name = "sub_emp_id"+taskTRId+"_"+cnt;
	    document.getElementById("sub_emp_id").id = "sub_emp_id"+cnt;
	    $("#sub_emp_id"+cnt).multiselect().multiselectfilter();
	    
	    document.getElementById("subpriority").name = "subpriority"+taskTRId;
	    document.getElementById("subpriority").id = "subpriority"+cnt;
	    
	    getSubTasksForDependency(cnt, taskTRId);
	    setSubDate(cnt);
	}
	 
	
	function getSubTasksForDependency(cnt, taskTRId) {
		var proId = document.getElementById("pro_id1").value;
		var taskId = document.getElementById("taskID"+taskTRId).value;
		getContent('subDependencySpan'+cnt, 'SaveTaskOrSubtaskAndGetId.action?proId='+proId+"&count="+cnt+"&taskId="+taskId+'&taskTRId='+taskTRId+'&type=GetSubTasks');
	}


function deleteSubTask(count) {
	if(confirm('Are you sure, you want to delete this sub task?')) {
		deleteSubTaskFromDB(count);
		var trIndex = document.getElementById("task_TR"+count).rowIndex;
	    document.getElementById("taskTable").deleteRow(trIndex);
	}
}


var dialogEdit1 = '#addTaskDescription';
function updateTaskDescription(cnt, divId, type) {
	/* removeLoadingDiv('the_div'); */
	var strTitle = "Task";
	if(type == 'ST') {
		strTitle = "Sub Task";
	}
	var taskDescription = document.getElementById(divId+cnt).value;
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html(strTitle+' Description');
	$.ajax({
		url : "AddTaskDescription.action?divId="+divId+"&count="+cnt+"&taskDescription="+encodeURIComponent(taskDescription),
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addTaskDescription(description, proId, divId, cnt, fromPage) {
	document.getElementById(divId+cnt).value = description;
	$("#modalInfo").hide();
}

function setDate(id) {
	//jQuery("#formID_3").validationEngine();
	$("#deadline1"+id).datepicker({
		format: 'dd/mm/yyyy',
		startDate : new Date(proStDate),
		endDate : new Date(proEdDate),
		autoclose: true
	}).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#startDate'+id).datepicker('setEndDate', minDate);
	});
	
	$("#startDate"+id).datepicker({
		format: 'dd/mm/yyyy',
		startDate : new Date(proStDate),
		endDate : new Date(proEdDate),
		autoclose: true
	}).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#deadline1'+id).datepicker('setStartDate', minDate);
	});
	
	<%-- $("#deadline1"+id).datepicker({
		format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("PROJECT_START_DATE_C")%>", maxDate: "<%=request.getAttribute("PROJECT_END_DATE_C")%>", 
		onClose: function(selectedDate){
			$("#startDate"+id).datepicker("option", "maxDate", selectedDate);
		}
	});	
	$("#startDate"+id).datepicker({
		format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("PROJECT_START_DATE_C")%>", maxDate: "<%=request.getAttribute("PROJECT_END_DATE_C")%>", 
		onClose: function(selectedDate){
			$("#deadline1"+id).datepicker("option", "minDate", selectedDate);
		}
	}); --%>
	
}


function setSubDate(id) {
	//jQuery("#formID_3").validationEngine();
	$("#subdeadline1"+id).datepicker({
		format: 'dd/mm/yyyy',
		startDate : new Date(proStDate),
		endDate : new Date(proEdDate),
		autoclose: true
	}).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#substartDate'+id).datepicker('setEndDate', minDate);
	});
	
	$("#substartDate"+id).datepicker({
		format: 'dd/mm/yyyy',
		startDate : new Date(proStDate),
		endDate : new Date(proEdDate),
		autoclose: true
	}).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#subdeadline1'+id).datepicker('setStartDate', minDate);
	});
	
	
	<%-- $("#subdeadline1"+id).datepicker({
		format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("PROJECT_START_DATE_C")%>", maxDate: "<%=request.getAttribute("PROJECT_END_DATE_C")%>", 
		onClose: function(selectedDate){
			$("#substartDate"+id).datepicker("option", "maxDate", selectedDate);
		}
	});	
	$("#substartDate"+id).datepicker({
		format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("PROJECT_START_DATE_C")%>", maxDate: "<%=request.getAttribute("PROJECT_END_DATE_C")%>", 
		onClose: function(selectedDate){
			$("#subdeadline1"+id).datepicker("option", "minDate", selectedDate);
		}
	}); --%>
	
}


function deleteRow(id) {
    try {
    	var row = document.getElementById(id);
		row.parentElement.removeChild(row); 
     } catch(e) {
        alert(e);
    }
}

 function checkTimeFilledEmpOfAllTasks() {
	 var taskCount = document.getElementById("taskcount").value;
	 var empCnt = 0;
	 var filledEmpCnt = 0;
	 for(var ii=0; ii<taskCount; ii++) {
		var timeFilledEmps = document.getElementById("tstFilledEmp"+ii).value;
		var timeFilledEmp = timeFilledEmps.split(",");
		var choice = "";
		if(document.getElementById("emp_id"+ii)) {
			choice = document.getElementById("emp_id"+ii);
		}
		if(document.getElementById("sub_emp_id"+ii)) {
			choice = document.getElementById("sub_emp_id"+ii);
		}
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
		var selectedEmp = exportchoice.split(",");
		for(var a=0; a<timeFilledEmp.length; a++) {
			console.log("timeFilledEmp[a]====="+timeFilledEmp[a]);
			if(timeFilledEmp[a] != '' && timeFilledEmp[a] != ' ') {
				console.log("selectedEmp.length====="+selectedEmp.length);
				for(var b=0; b<selectedEmp.length; b++) {
					console.log("selectedEmp[b]====="+selectedEmp[b]);
					if(timeFilledEmp[a] == selectedEmp[b]) {
						empCnt++;
					}
				}
				filledEmpCnt++;
			}
		}
	 }
	if(filledEmpCnt == empCnt) {
		return true;
	} else {
		alert("Resource already fill timesheet against this task, so can't remove resource from this task.");
		return false;
	}
 }
 
 
	function setValue(strName) {
		var val = document.getElementById(strName).value;
		if(val == '0') {
			document.getElementById(strName).value = '1';
		} else {
			document.getElementById(strName).value = '0';
		}
	}
 
 
	function setDependencyPeriod(val, cnt, type) {
		if(type == 'Task') {
			var taskId = document.getElementById("dependency"+cnt).value;
			if(parseFloat(taskId) > 0) {
				var taskCnt = document.getElementById(+taskId).value;
				var taskStDt = document.getElementById("startDate"+taskCnt).value;
				var taskEndDt = document.getElementById("deadline1"+taskCnt).value;
				if(val == '0') {
					document.getElementById("startDate"+cnt).value = taskStDt;
					document.getElementById("deadline1"+cnt).value = "";
				} else if(val == '1') {
					document.getElementById("startDate"+cnt).value = taskEndDt;
					document.getElementById("deadline1"+cnt).value = "";
				}
			}
		} else {
			var subtaskId = document.getElementById("subDependency"+cnt).value;
			if(parseFloat(subtaskId) > 0) {
				var subtaskCnt = document.getElementById(subtaskId).value;
				var subtaskStDt = document.getElementById("substartDate"+subtaskCnt).value;
				var subtaskEndDt = document.getElementById("subdeadline1"+subtaskCnt).value;
				if(val == '0') {
					document.getElementById("substartDate"+cnt).value = subtaskStDt;
					document.getElementById("subdeadline1"+cnt).value = "";
				} else if(val == '1') {
					document.getElementById("substartDate"+cnt).value = subtaskEndDt;
					document.getElementById("subdeadline1"+cnt).value = "";
				}
			}
		}
	}
	
 
	function setDependencyDate(val, stDate, endDate, minDate, maxDate) {
	//	jQuery("#formID_3").validationEngine();
		var proStDt = '<%=request.getAttribute("PROJECT_START_DATE_MM_DD")%>';
		var proEndDt = '<%=request.getAttribute("PROJECT_END_DATE_MM_DD")%>';
		
		if(val == '0') {
			proStDt = minDate;
		} else if(val == '1') {
			proStDt = maxDate;
		}
		$("#deadline1"+endDate).datepicker({
			format : 'dd/mm/yyyy', minDate: '', maxDate: '', 
			onClose: function(selectedDate){
				$("#startDate"+stDate).datepicker("option", "maxDate", selectedDate);
			}
		});
	}
	
	function executeTaskActions(val, parentVal, cnt, fillData, taskId, isRecurr) {
		if(val == '1') {
			if(parseFloat(fillData) > 0) {
				alert('You can not delete this task as user has already booked the time against this task.');
			} else {
				deleteTask(cnt);
			}
		} else if(val == '3') {
			if(confirm('Are you sure, you wish to repeat this task?')) {
				repeatTask(cnt, isRecurr);
			}
		} else if(val == '4') {
			if(parseFloat(fillData) > 0) {
				alert('You can not add sub task as user has already booked the time against this task.');
			} else {
				if(confirm('Are you sure, you wish to add new sub-task?')) {
					addNewSubTask(cnt, parentVal, isRecurr);
				}
			}
		}
		document.getElementById("taskActions"+cnt).selectedIndex = '0';
	}
	
	
	function executeSubTaskActions(val, parentVal, cnt, fillData, taskTRId, subtaskId, isRecurr) {
		if(val == '1') {
			if(parseFloat(fillData) > 0) {
				alert('You can not delete this sub-task as user has already booked the time against this sub-task.');
			} else {
				deleteSubTask(cnt);
			}
		} else if(val == '3') {
			if(confirm('Are you sure, you wish to repeat this sub-task?')) {
				repeatSubTask(cnt, taskTRId, parentVal, isRecurr);
			}
		} else if(val == '4') {
			if(confirm('Are you sure, you wish to add new sub-task?')) {
				addNewSubTask(taskTRId, parentVal, isRecurr);
			}
		}
		document.getElementById("subtaskActions"+cnt).selectedIndex = '0';
	}
	
	
	function submitAndProcced(formId, step) {
		if(checkValidationStep3() && checkTimeFilledEmpOfAllTasks()) {
			
			if(document.getElementById("submitBtnTable_"+step)) {
				document.getElementById("submitBtnTable_"+step).style.display = "none";
			}
			var form_data = $("#"+formId).serialize();
			$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'PreAddNewProject1.action?submit=SubmitAndProceed',
				data: form_data,
				success: function(result) {
					$("#subSubDivResult").html(result);
					document.getElementById("proDetail").className = "";
					document.getElementById("proSnapshot").className = "";
					document.getElementById("proStep1").className = "";
					document.getElementById("proStep2").className = "";
					document.getElementById("proStep3").className = "";
					document.getElementById("proStep4").className = "";
					/* document.getElementById("proStep5").className = ""; */
					document.getElementById("proStep6").className = "";
					document.getElementById("proStep7").className = "";
					document.getElementById("proStep8").className = "";
					var intStep = (parseInt(step)+1);
					 if(step == 4) {
					 	intStep = (parseInt(step)+2);
					 }
					document.getElementById("proStep"+intStep).className = "active";
		   		}
			});
		} else {
			return false;
		}
	}
	
	
	function saveAndExit(formId, step) {
		if(checkValidationStep3() && checkTimeFilledEmpOfAllTasks()) {
			if(document.getElementById("submitBtnTable_"+step)) {
				document.getElementById("submitBtnTable_"+step).style.display = "none";
			}
			var proType = document.getElementById("proType").value;
			var form_data = $("#"+formId).serialize();
			$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'PreAddNewProject1.action?submit=SaveAndExit',
				data: form_data,
				success: function(result) {
					/* $("#subSubDivResult").html(result); */
					getAllProjectNameList('AllProjectNameList', proType);
				}
			});
		} else {
			return false;
		}
	}
	
	
</script>

	<style>
.formcss select,.formcss button {
	width: 150px !important;
}
</style>

	<script src="<%= request.getContextPath()%>/scripts/color.js"
		type="text/javascript"></script>

	<script type="text/JavaScript">
		var cp = new ColorPicker('window'); 
		var cp2 = new ColorPicker('window');
	</script>

	<s:form id="frmAddProject_3" cssClass="formcss" action="PreAddNewProject1" name="frmAddProject_3" method="post" theme="simple" onsubmit="return checkTimeFilledEmpOfAllTasks();"> <!--  enctype="multipart/form-data" onsubmit="return checkTimeFilledEmpOfAllTasks()"  -->
		<div class="col-lg-12 col-md-12 col-sm-12">
			<s:hidden name="step" value="3"></s:hidden>
			<s:hidden name="operation"></s:hidden>
			<s:hidden name="pageType"></s:hidden>
			<s:hidden name="proType" id="proType"></s:hidden>
			<s:hidden name="pro_id" id="pro_id1"></s:hidden>
			<%
				Map<String, List<String>> hmProTasks = (Map<String, List<String>>)request.getAttribute("hmProTasks");
				if(hmProTasks == null)hmProTasks = new LinkedHashMap<String, List<String>>();
				
				Map<String, List<List<String>>> hmProSubTasks = (Map<String, List<List<String>>>) request.getAttribute("hmProSubTasks");
				if(hmProSubTasks == null)hmProSubTasks = new LinkedHashMap<String, List<List<String>>>();
				
				List<FillDependentTaskList> dependencyList = (List<FillDependentTaskList>)request.getAttribute("dependencyList");
				List<GetDependancyTypeList> dependancyTypeList = (List<GetDependancyTypeList>)request.getAttribute("dependancyTypeList");
				
				List<GetPriorityList> priorityList = (List<GetPriorityList>)request.getAttribute("priorityList");
				List<FillTaskEmpList> TaskEmpNamesList = (List<FillTaskEmpList>)request.getAttribute("TaskEmpNamesList");
				List<FillSkills> empSkillList = (List<FillSkills>)request.getAttribute("empSkillList");
			
				String costLbl = "Hr";
				String estimateLbl = "hours";
				if((String)request.getAttribute("PROJECT_CALC_TYPE") != null && "D".equalsIgnoreCase((String)request.getAttribute("PROJECT_CALC_TYPE"))) {
					costLbl = "Day";
					estimateLbl = "days";
				} else if((String)request.getAttribute("PROJECT_CALC_TYPE") != null && "M".equalsIgnoreCase((String)request.getAttribute("PROJECT_CALC_TYPE"))) {
					costLbl = "Month";
					estimateLbl = "months";
				}
			
				String BILL_FREQUENCY = (String)request.getAttribute("BILL_FREQUENCY");
				
				String isRecurr = "N";
				if(BILL_FREQUENCY != null && !BILL_FREQUENCY.equals("O")) {
					isRecurr = "Y";
				}
			%>
			<div class="scroll" style="width: 100%; overflow-x: auto;">
			<div><b>**</b> This is estimated time. A manager perceives, the task would take to be delivered.</div>
				<table id="taskTable" border="0" class="table table_no_border">
					<tr>
						<th>Task Name<sup>*</sup></th>
						<th>Dependency</th>
						<th>Dependency Type</th>
						<th>Priority</th>
						<th>Employee<sup>*</sup></th>
						<th>Start Date<sup>*</sup></th>
						<th>End Date<sup>*</sup></th>
						<th> <%="Estimated man-"+estimateLbl %><sup>*</sup> **</th>
						<th>Billable</th>
						<th>Color<sup>*</sup></th>
						<th>Actions</th>
					</tr>

					<% 
					Iterator<String> it = hmProTasks.keySet().iterator();
					int i = 0;
					int taskTRId = 0;
					while(it.hasNext()) {
					String taskId = it.next();	
					List<String> alInner = hmProTasks.get(taskId);
					taskTRId = i;
				%>
					<tr id="task_TR<%=i%>">
						<td valign="top"><input type="hidden" name="taskTRId" id="taskTRId<%=i %>" value="<%=taskTRId %>" /> 
						<input type="hidden" name="tstFilledEmp" id="tstFilledEmp<%=i %>" value="<%=alInner.get(14) %>" /> 
						<input type="hidden" name="taskDescription" id="taskDescription<%=i %>" value="<%=alInner.get(16) %>" /> 
						<textarea name="taskname" id="taskname<%=i %>" class="validateRequired" style="width: 160px !important;" 
						<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
						onchange="saveTaskAndGetTaskId(0);" <% } %>><%=alInner.get(3) %></textarea>
						<%-- <input type="text" name="taskname" id="taskname<%=i %>" value="<%=alInner.get(3) %>" class="validateRequired" style="width: 160px !important;"
							<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
							onchange="saveTaskAndGetTaskId(0);" <% } %> /> --%>
							<div id="addTaskSpan<%=i %>" style="display: block;">
								<input type="hidden" name="taskID" id="taskID<%=i %>" value="<%=alInner.get(0)%>" /> 
								<input type="hidden" name="<%=alInner.get(0) %>" id="<%=alInner.get(0) %>" value="<%=i %>" />
							</div> <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
							<div>
								<a href="javascript:void(0)" onclick="updateTaskDescription('<%=i %>', 'taskDescription', 'T')">D</a>
								<% if(BILL_FREQUENCY != null && !BILL_FREQUENCY.equals("O")) { %>
								<% 
									String strChecked = "";
										if(uF.parseToInt(alInner.get(17)) == 1) { 
											strChecked = "checked";
										}
									%>
								&nbsp;<input type="checkbox" name="recurringTask" id="recurringTask<%=i %>" <%=strChecked %> onclick="setValue('isRecurringTask<%=i %>');" title="Add task to recurring in next frequency" />Recurr Task
								<% } %>
								<input type="hidden" name="isRecurringTask" id="isRecurringTask<%=i %>" value="<%=alInner.get(17) %>" />
							</div> <% } %>
						</td>

						<td valign="top"><select name="dependency" id="dependency<%=i %>" style="width: 135px !important;">
								<option value="">Select Dependency</option>
								<%=alInner.get(4)%>
						</select>
						</td>

						<td valign="top"><select name="dependencyType" id="dependencyType<%=i %>" style="width: 135px !important;" onchange="setDependencyPeriod(this.value, '<%=i %>', 'Task');">
								<option value="">Select Dependency Type</option>
								<option value="0" <%if(alInner.get(5) != null && alInner.get(5).equals("0")) { %>
									selected <% } %>>Start-Start</option>
								<option value="1" <%if(alInner.get(5) != null && alInner.get(5).equals("1")) { %>
									selected <% } %>>Finish-Start</option>
						</select>
						</td>

						<td valign="top"><select name="priority" id="priority<%=i%>" style="width: 80px !important;" class="validateRequired">
								<% for(GetPriorityList getPriorityList:priorityList) { %>
								<option value="<%=getPriorityList.getPriId() %>"
									<%if(alInner.get(6) != null && getPriorityList.getPriId().equals(alInner.get(6))) { %>
									selected <%} %>>
									<%=getPriorityList.getProName() %></option>
								<% } %>
						</select>
						</td>

						<td valign="top"><script type="text/javascript">
							$(function() {
								$("#emp_id<%=i %>").multiselect().multiselectfilter();
							});
						</script>
							<span id="empSpan<%=i %>"> <select name="emp_id<%=taskTRId %>" id="emp_id<%=i %>" style="width: 0px !important;" class="validateRequired" multiple="multiple">
									<!-- <option value="">Select Employee</option> -->
									<%=alInner.get(8)%>
					 		</select> </span>
						</td>
						<td valign="top"><input type="text" id="startDate<%=i %>" name="startDate" style="width: 90px !important;" class="validateRequired" value="<%=alInner.get(9)%>"></td>
						<td valign="top"><input type="text" id="deadline1<%=i %>" name="deadline1" value="<%=alInner.get(10)%>" class="validateRequired" style="width: 90px !important;">
						</td>
						<td valign="top"><input type="text" name="idealTime" id="idealTime<%=i %>" onkeypress="return isNumberKey(event)" value="<%=alInner.get(11)%>" class="validateRequired" style="width: 50px !important; text-align: right;"></td>

						<td valign="top">
							<% 
								String strBillableChecked = "";
								String strBillableValue = "0";
								if(uF.parseToBoolean(alInner.get(18))) { 
									strBillableChecked = "checked";
									strBillableValue = "1";
								}
							%>
							<input type="checkbox" name="billableTask" id="billableTask<%=i %>" <%=strBillableChecked %> onclick="setValue('isBillableTask<%=i %>');" title="Please tick for billable" />
							<input type="hidden" name="isBillableTask" id="isBillableTask<%=i %>" value="<%=strBillableValue %>" />
						</td>

						<td valign="top"><input type="text" name="colourCode" value="<%=alInner.get(12)%>" id="colourCode<%=i %>" class="validateRequired" style="width:10px !important; background-color: <%=alInner.get(12)%>" readonly="readonly" /> <%-- <img align="left" style="cursor: pointer; position: absolute; padding: 5px 0 0 5px" src="images1/color_palate.png" id="pick1" onclick="cp2.select(document.getElementById('formID_3').colourCode<%=i %>,'pick1'); return false;" /> --%>
						</td>

						<td width="150px"><select name="taskActions<%=i %>" id="taskActions<%=i %>" style="width: 100px !important;" onchange="executeTaskActions(this.value, this.parentNode.parentNode.rowIndex, '<%=i %>', '<%=uF.parseToDouble(alInner.get(13)) %>', '<%=alInner.get(0)%>', '<%=isRecurr %>');">
								<option value="">Actions</option>
								<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
								<option value="1">Delete</option>
								<option value="3">Repeat Task</option>
								<option value="4">Add Sub-task</option>
								<% } %>
						</select></td>
					</tr>

					<% 	i++; 	
						List<List<String>> proSubTaskList = hmProSubTasks.get(taskId);
						for(int j=0; proSubTaskList != null && j<proSubTaskList.size(); j++) {
							List<String> innerList = proSubTaskList.get(j);
				%>

					<tr id="task_TR<%=i%>">
						<td valign="top" align="right"><input type="hidden" name="TskTRId<%=taskTRId %>" id="TskTRId<%=taskTRId %>" value="<%=taskTRId %>"> 
						<input type="hidden" name="subTaskTRId<%=taskTRId %>" id="subTaskTRId<%=i %>" value="<%=i %>"> 
						<input type="hidden" name="tstFilledEmp" id="tstFilledEmp<%=i %>" value="<%=innerList.get(14) %>" /> 
						<input type="hidden" name="subTaskDescription<%=taskTRId %>" id="subTaskDescription<%=i %>" value="<%=innerList.get(15) %>" />
						<textarea name="subtaskname<%=taskTRId %>" id="subtaskname<%=i %>" class="validateRequired" style="width: 120px !important;" 
						<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
							onchange="saveSubTaskAndGetSubTaskId('<%=i %>', '<%=taskTRId %>');" <% } %> ><%=innerList.get(3) %></textarea>
						<%-- <input type="text" name="subtaskname<%=taskTRId %>" id="subtaskname<%=i %>" value="<%=innerList.get(3) %>" class="validateRequired" style="width: 120px !important;"
							<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
							onchange="saveSubTaskAndGetSubTaskId('<%=i %>', '<%=taskTRId %>')"<% } %> /> --%> 
						<span id="addSubTaskSpan<%=i %>"> <input type="hidden" name="subTaskID<%=taskTRId %>" id="subTaskID<%=i %>" value="<%=innerList.get(0)%>"> 
						<input type="hidden" name="<%=alInner.get(0) %>" id="<%=alInner.get(0) %>" value="<%=i %>" /> 
						</span> <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
							<div>
								<a href="javascript:void(0)" onclick="updateTaskDescription('<%=i %>', 'subTaskDescription', 'ST')">D</a>
								<% if(BILL_FREQUENCY != null && !BILL_FREQUENCY.equals("O")) { %>
								<% 
										String strSTChecked = "";
											if(uF.parseToInt(innerList.get(16)) == 1) { 
												strSTChecked = "checked";
											}
									%>
								&nbsp;<input type="checkbox" name="recurringSubTask<%=taskTRId %>" id="recurringSubTask<%=i %>" <%=strSTChecked %> onclick="setValue('isRecurringSubTask<%=i %>');" title="Add sub task to recurring in next frequency" />Recurr Subtask
								<% } %>
								<input type="hidden" name="isRecurringSubTask<%=taskTRId %>" id="isRecurringSubTask<%=i %>" value="<%=innerList.get(16) %>" />
							</div> <% } %>
						</td>

						<td valign="top"><select name="subDependency<%=taskTRId %>" id="subDependency<%=i %>" style="width: 135px !important;">
								<option value="">Select Dependency</option>
								<%=innerList.get(4) %>
						</select>
						</td>

						<td valign="top"><select name="subDependencyType<%=taskTRId %>" id="subDependencyType<%=i %>" style="width: 135px !important;" onchange="setDependencyPeriod(this.value, '<%=i %>', 'SubTask');">
								<option value="">Select Dependency Type</option>
								<option value="0" <%if(innerList.get(5) != null && innerList.get(5).equals("0")) { %>
									selected <% } %>>Start-Start</option>
								<option value="1" <%if(innerList.get(5) != null && innerList.get(5).equals("1")) { %>
									selected <% } %>>Finish-Start</option>
						</select>
						</td>

						<td valign="top"><select name="subpriority<%=taskTRId %>" id="subpriority<%=i%>" style="width: 80px !important;" class="validateRequired">
								<% for(GetPriorityList getPriorityList:priorityList) { %>
								<option value="<%=getPriorityList.getPriId() %>"
									<%if(innerList.get(6) != null && getPriorityList.getPriId().equals(innerList.get(6))) { %>
									selected <% } %>>
									<%=getPriorityList.getProName() %></option>
								<% } %>
						</select>
						</td>

						<td valign="top"><script type="text/javascript">
							$(function() {
								$("#sub_emp_id<%=i %>").multiselect().multiselectfilter();
							});
						</script> <span id="subEmpSpan<%=i %>"> <select name="sub_emp_id<%=taskTRId %>_<%=i %>" id="sub_emp_id<%=i %>" style="width: 0px !important;" class="validateRequired" multiple="multiple">
									<!-- <option value="">Select Employee</option> -->
									<%=innerList.get(8) %>
							</select> </span>
						</td>
						<td valign="top"><input type="text" id="substartDate<%=i %>" name="substartDate<%=taskTRId %>" style="width: 90px !important;" class="validateRequired" value="<%=innerList.get(9)%>"></td>
						<td valign="top"><input type="text" id="subdeadline1<%=i %>" name="subdeadline1<%=taskTRId %>" value="<%=innerList.get(10)%>" class="validateRequired" style="width: 90px !important;">
						</td>
						<td valign="top"><input type="text" name="subidealTime<%=taskTRId %>" id="subidealTime<%=i %>" onkeypress="return isNumberKey(event)" value="<%=innerList.get(11)%>" class="validateRequired" style="width: 50px !important; text-align: right;"></td>
						
						<td valign="top">
						<% 
								String strBillableSTChecked = "";
								String strBillableSTValue = "0";
								if(uF.parseToBoolean(innerList.get(17))) { 
									strBillableSTChecked = "checked";
									strBillableSTValue = "1";
								}
							%>
							<input type="checkbox" name="billableSubTask" id="billableSubTask<%=i %>" <%=strBillableSTChecked %> onclick="setValue('isBillableSubTask<%=i %>');" title="Please tick for billable" />
							<input type="hidden" name="isBillableSubTask<%=taskTRId %>" id="isBillableSubTask<%=i %>" value="<%=strBillableSTValue %>" />
						</td>
						
						<td valign="top"><input type="text" name="subcolourCode<%=taskTRId %>" value="<%=innerList.get(12)%>" id="subcolourCode<%=i %>" class="validateRequired" style="width:10px !important; background-color: <%=innerList.get(12)%>" readonly="readonly" /> <%-- <img align="left" style="cursor: pointer; position: absolute; padding: 5px 0 0 5px" src="images1/color_palate.png" id="pick1" onclick="cp2.select(document.getElementById('formID_3').subcolourCode<%=i %>,'pick1'); return false;" /> --%>
						</td>

						<td><select name="subtaskActions<%=i %>" id="subtaskActions<%=i %>" style="width: 100px !important;" onchange="executeSubTaskActions(this.value, this.parentNode.parentNode.rowIndex, '<%=i %>', '<%=uF.parseToDouble(innerList.get(13)) %>', '<%=taskTRId %>', '<%=innerList.get(0)%>', '<%=isRecurr %>');">
								<option value="">Actions</option>
								<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
								<option value="1">Delete</option>
								<option value="3">Repeat Sub-task</option>
								<option value="4">Add Sub-task</option>
								<% } %>
						</select></td>

					</tr>
					<% 	i++;
						}
					}	
					%>
					<div>
						<input type="hidden" name="taskcount" id="taskcount" value="<%=i %>" />
					</div>
					<%  if(hmProTasks == null || hmProTasks.isEmpty() || hmProTasks.size()==0) { %>

					<tr id="task_TR0">
						<td><input type="hidden" name="taskTRId" id="taskTRId0" value="0" /> 
							<input type="hidden" name="taskDescription" id="taskDescription0" value="" /> 
							<textarea name="taskname" id="taskname0" class="validateRequired" style="width: 160px !important;" 
							<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
							onchange="saveTaskAndGetTaskId(0);" <% } %>></textarea>
							<!-- <input type="text" name="taskname" id="taskname0" class="validateRequired" style="width: 160px !important;" onchange="saveTaskAndGetTaskId(0);" /> -->
							<div id="addTaskSpan0" style="display: block;">
								<input type="hidden" name="taskID" id="taskID0" value="0" />
							</div> <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
							<div>
								<a href="javascript:void(0)" onclick="updateTaskDescription('0', 'taskDescription', 'T')">D</a>
								<% if(BILL_FREQUENCY != null && !BILL_FREQUENCY.equals("O")) { %>
								&nbsp;<input type="checkbox" name="recurringTask" id="recurringTask_0" onclick="setValue('isRecurringTask_0');" title="Add task to recurring in next frequency" />Recurr Task
								<% } %>
								<input type="hidden" name="isRecurringTask" id="isRecurringTask_0" value="0" />
							</div> <% } %>
						</td>

						<td><s:select name="dependency" id="dependency0" listKey="dependencyId" headerKey="" headerValue="Select Dependency" listValue="dependencyName" list="dependencyList" key="" cssStyle="width:135px !important;" />
						</td>

						<td><s:select name="dependencyType" id="dependencyType0" listKey="dependancyTypeId" headerKey="" headerValue="Select Dependency Type" listValue="dependancyTypeName" list="dependancyTypeList" key="" cssStyle="width:135px !important;" />
						</td>

						<td><s:select label="Select Priority" name="priority" id="priority0" cssClass="validateRequired" listKey="priId" listValue="proName" list="priorityList" key="" cssStyle="width:80px !important;" /> <!-- headerKey="" headerValue="Select Priority" -->
						</td>

						<td><script type="text/javascript">
							$(function() {
								$("#emp_id0").multiselect().multiselectfilter();
							});
						</script> <span id="empSpan0"> <s:select name="emp_id0" id="emp_id0" cssClass="validateRequired" listKey="TaskEmployeeId" listValue="TaskEmployeeName" list="TaskEmpNamesList" key="" required="true" cssStyle="width:0px !important;" multiple="true" /> </span>
						</td>

						<td><input type="text" id="startDate0" name="startDate" style="width: 90px !important;" value="<%=request.getAttribute("PROJECT_START_DATE_C")%>" class="validateRequired">
						</td>
						<td><input type="text" id="deadline10" name="deadline1" value="<%=request.getAttribute("PROJECT_END_DATE_C")%>" class="validateRequired" style="width: 90px !important;">
						</td>
						<td><input type="text" name="idealTime" id="idealTime0" onkeypress="return isNumberKey(event)" class="validateRequired" style="width: 50px !important; text-align: right;"></td>

						<td valign="top">
							<input type="checkbox" name="billableTask" id="billableTask0" onclick="setValue('isBillableTask0');" title="Please tick for billable" />
							<input type="hidden" name="isBillableTask" id="isBillableTask0" value="0" />
						</td>

						<% String myColor = "#C2AD99"; %>
						<td><input type="text" name="colourCode" id="colourCode0" class="validateRequired" style="width: 10px !important; background-color: <%=myColor %>;" value="<%=myColor %>" readonly="readonly" /> <!-- <img align="left" style="cursor: pointer; position: absolute; padding: 5px 0 0 5px" src="images1/color_palate.png" id="pick1" onclick="cp2.select(document.getElementById('formID_3').colourCode0,'pick1'); return false;" /> -->
						</td>

						<td width="150px"><select name="taskActions0" id="taskActions0" style="width: 100px !important;" onchange="executeTaskActions(this.value, this.parentNode.parentNode.rowIndex, '0', '', '', '<%=isRecurr %>');">
								<option value="">Actions</option>
								<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
								<option value="1">Delete</option>
								<option value="3">Repeat Task</option>
								<option value="4">Add Sub-task</option>
								<% } %>
						</select></td>
					</tr>
					<% } %>
				</table>
			</div>
			<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
			<div>
				<a href="javascript:void(0)" onclick="addNewTask('<%=isRecurr %>');" title="Add new task">+Add New Task</a>
			</div>
			<% } %>
		</div>
		<div class="clr"></div>

		<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
		<div style="margin: 0px 0px 0px 120px">
			<table id="submitBtnTable_3" border="0" class="table table_no_border">
				<tr>
					<td>
						<% if(operation != null && operation.equals("E")) { %> 
							<input type="button" name="skipProcced" value="Skip & Proceed" class="btn btn-primary" style="float: right; margin-right: 5px;" onclick="skipAndProcced('<%=request.getAttribute("pro_id") %>','3', '<%=pageType %>');" />
						<% } %>
						<input type="submit" name="submit" value="Submit & Proceed" class="btn btn-primary cstm-validate" style="float: right; margin-right: 5px;" onclick="submitAndProcced('frmAddProject_3', '3');" /> 
						<input type="submit" name="stepSave" value="Save & Exit" class="btn btn-primary cstm-validate" style="float: right; margin-right: 5px;" onclick="saveAndExit('frmAddProject_3', '3');" /> 
						<input type="button" value="Cancel" class="btn btn-danger" style="float: right; margin-right: 5px;" name="cancel" onclick="closeForm('<%=pageType %>');" /></td>
					<td></td>
				</tr>
			</table>
		</div>
		<% } %>
	</s:form>
	
	<script>
	
		$(".cstm-validate").click(function(){
			$('.validateRequired').filter(':hidden').prop('required',false);
			$('.validateRequired').filter(':visible').prop('required',true);
		});
    
	</script>
</s:if>


<s:if test="step==4">

<style>
	.formcss select,.formcss button{
		width: 125px !important;
	}
</style>

	<script>
	jQuery(document).ready(function() {
		$(".validateRequired").prop('required',true);
	});
	
	
	function openCloseDocs(folderCnt,subFolderSize,docFolderSize) {
		if(document.getElementById("hideFolder_"+folderCnt)) {
			var status = document.getElementById("hideFolder_"+folderCnt).value;
			if(status == '0') {
				for(var i=0; i< parseInt(subFolderSize);i++){
					document.getElementById("folderTR_"+folderCnt+"_"+i).style.display = "block";
				}
				for(var i=0; i< parseInt(docFolderSize);i++){
					document.getElementById("docFolderTR_"+folderCnt+"_"+i).style.display = "block";
				}
				
				document.getElementById("hideFolder_"+folderCnt).value = '1';
				if(document.getElementById("FDDownarrowSpan"+folderCnt)) {
					document.getElementById("FDDownarrowSpan"+folderCnt).style.display = 'none';
					document.getElementById("FDUparrowSpan"+folderCnt).style.display = 'block';
				}
			} else {
				for(var i=0; i< parseInt(subFolderSize);i++){
					document.getElementById("folderTR_"+folderCnt+"_"+i).style.display = "none";
				}
				for(var i=0; i< parseInt(docFolderSize);i++){
					document.getElementById("docFolderTR_"+folderCnt+"_"+i).style.display = "none";
				}
				
				document.getElementById("hideFolder_"+folderCnt).value = '0';
				if(document.getElementById("FDDownarrowSpan"+folderCnt)) {
					document.getElementById("FDDownarrowSpan"+folderCnt).style.display = 'block';
					document.getElementById("FDUparrowSpan"+folderCnt).style.display = 'none';
				}
			}
		}
	}
	
	
	function openCloseDocs1(folderCnt,subFolderCnt,subDocSize) {
		if(document.getElementById("hideFolder_"+folderCnt+"_"+subFolderCnt)) {
			var status = document.getElementById("hideFolder_"+folderCnt+"_"+subFolderCnt).value;
			if(status == '0') {
				for(var i=0; i< parseInt(subDocSize);i++) {
					document.getElementById("folderTR_"+folderCnt+"_"+subFolderCnt+"_"+i).style.display = "block";
				}
				
				document.getElementById("hideFolder_"+folderCnt+"_"+subFolderCnt).value = '1';
				if(document.getElementById("FDDownarrowSpan"+folderCnt+"_"+subFolderCnt)) {
					document.getElementById("FDDownarrowSpan"+folderCnt+"_"+subFolderCnt).style.display = 'none';
					document.getElementById("FDUparrowSpan"+folderCnt+"_"+subFolderCnt).style.display = 'block';
				}
			} else {
				for(var i=0; i< parseInt(subDocSize);i++){
					document.getElementById("folderTR_"+folderCnt+"_"+subFolderCnt+"_"+i).style.display = "none";
				}
				
				document.getElementById("hideFolder_"+folderCnt+"_"+subFolderCnt).value = '0';
				if(document.getElementById("FDDownarrowSpan"+folderCnt+"_"+subFolderCnt)) {
					document.getElementById("FDDownarrowSpan"+folderCnt+"_"+subFolderCnt).style.display = 'block';
					document.getElementById("FDUparrowSpan"+folderCnt+"_"+subFolderCnt).style.display = 'none';
				}
			}
		}
	}
		
		function deleteProjectDocs(type, divName, folderName, proDocID, mainPath) {
			var msg ="Are you sure, you wish to delete this Folder?";
			if(type != 'F' && type != 'SF') {
				msg ="Are you sure, you wish to delete this File?";
			}
			if(confirm(msg)) {
				var xmlhttp;
				if (window.XMLHttpRequest) {
			        // code for IE7+, Firefox, Chrome, Opera, Safari
			        xmlhttp = new XMLHttpRequest();
				}
			    if (window.ActiveXObject) {
			        // code for IE6, IE5
			    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
			    }

			    if (xmlhttp == null) {
			            alert("Browser does not support HTTP Request");
			            return;
			    } else {
		            var xhr = $.ajax({
		                url : 'DeleteProjectDocuments.action?operation=D&type='+type+'&proDocID='+proDocID +'&mainPath='+encodeURIComponent(mainPath),
		                cache : false,
		                success : function(data) {
							if(data = 'yes') {
								document.getElementById(divName).innerHTML = '';
							}
		                }
		            });
			    }
			}
		}
		

		function executeFolderActions(val, clientId, proId, folderName, proFolderId, type, filePath, fileDir, divName, savePath) {
			if(val == '1') {
				if(type == 'F' || type == 'SF') {
					editFolder(clientId, proId, folderName, proFolderId, type);
				} else {
					editDoc(clientId, proId, folderName, proFolderId, type, filePath, fileDir);
				}
			} else if(val == '2') {
				deleteProjectDocs(type, divName, folderName, proFolderId, savePath);
			}
		}
		
		
	function editFolder(clientId, proId, folderName, proFolderId, type) {
		
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Update '+folderName+' Folder');
		$.ajax({
			url : "UpdateProjectDocumentFolder.action?clientId="+clientId+"&proId="+proId+"&folderName="+folderName+"&proFolderId="+proFolderId+"&type="+type,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function editDoc(clientId, proId, docName, proFolderId,type,filePath,fileDir) {
		
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Update '+docName+' file');
		$.ajax({
			url : "UpdateProjectDocumentFile.action?clientId="+clientId+"&proId="+proId+"&folderName="+docName+"&proFolderId="+proFolderId+"&type="+type+"&filePath="+encodeURIComponent(filePath)+"&fileDir="+encodeURIComponent(fileDir),
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
		
	}
	
	
	function projectDocFact(clientId, proId, docName, proFolderId,type,filePath,fileDir) {
		
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('View '+docName+' file');
		$.ajax({
			url : "ProjectDocumentFact.action?clientId="+clientId+"&proId="+proId+"&folderName="+docName+"&proFolderId="+proFolderId+"&type="+type+"&filePath="+encodeURIComponent(filePath)+"&fileDir="+encodeURIComponent(fileDir),
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
		
	}
	
	
	function checkStatus(strCheckBox, hideId) {
		if(strCheckBox.checked == true) {
			document.getElementById(hideId).value = '1';
		} else {
			document.getElementById(hideId).value = '0';
		}
	}
	
	
	function clearData(elementId) {
		var val = document.getElementById(elementId).value;
		if(val=="Description" || val=="Folder Name" || val=="Document Scope" || val=="Sub Folder Name" ) {
			document.getElementById(elementId).value = '';
			document.getElementById(elementId).style.color = '';
		}
	}
	
	
	function fillData(elementId, num) {
		if(document.getElementById(elementId).value=='' && num==1){
			document.getElementById(elementId).value="Description";
			document.getElementById(elementId).style.color = 'gray';
		} else if(document.getElementById(elementId).value=='' && num==2){
			document.getElementById(elementId).value="Folder Name";
			document.getElementById(elementId).style.color = 'gray';
		} else if(document.getElementById(elementId).value=='' && num==3){
			document.getElementById(elementId).value="Document Scope";
			document.getElementById(elementId).style.color = 'gray';
		} else if(document.getElementById(elementId).value=='' && num==4){
			document.getElementById(elementId).value="Sub Folder Name";
			document.getElementById(elementId).style.color = 'gray';
		}
	}
	
	var proTasks = '<%=request.getAttribute("sbProTasks")%>'; 
	var proEmployee = '<%=request.getAttribute("sbProEmp")%>';
	var proCategory = '<%=request.getAttribute("sbProCategory")%>';
	var proPoc = '<%=request.getAttribute("sbProSPOC")%>';
	function addNewFolder(tableName, rowCountName) { 
		var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    
		    row.id="folderTR"+cnt;
		    var cell0 = row.insertCell(0);
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    cell0.innerHTML = "<span style=\"float:left; margin-right: 9px;\"><input type=\"hidden\" name=\"folderTRId\" id=\"folderTRId"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strFolderName\" id=\"strFolderName"+cnt+"\" style=\"width: 200px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 2);\" value=\"Folder Name\"/></span>"
	        +"<span style=\"float:left;\"><a href=\"javascript:void(0)\" onclick=\"addNewFolder('"+tableName+"', '"+rowCountName+"')\" class=\"fa fa-fw fa-plus\" title=\"Create New Folder \">&nbsp;</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteFolder('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Folder\">&nbsp;</a></span>"
	        +"<span style=\"float:left; width: 100%; margin-top: 5px;\"><textarea rows=\"3\" name=\"strFolderDescription\" id=\"strFolderDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
	        +"<span style=\"float:left; width: 100%;\"><a href=\"javascript:void(0);\" style=\"margin: -15px 0px 15px 50px; \" onclick=\"addNewSubFolder('"+cnt+"', this.parentNode.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"');\"> +Add Folder</a>"
	        +"<a href=\"javascript:void(0);\" style=\"margin: -15px 0px 15px 15px; \" onclick=\"addNewFolderDocs('"+cnt+"', this.parentNode.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"');\"> +Add Document</a></span>";
	        
		    var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeFolder\" id=\"proCategoryTypeFolder"+cnt+"\" style=\"width:100px !important;\" onchange=\"changeCategoryType(this.value, 'proCatagorySpan"+cnt+"', 'proTaskSpan"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proCatagorySpan"+cnt+"\" style=\"float: left; display: none\"><select name=\"proFolderCategory"+cnt+"\" id=\"proFolderCategory"+cnt+"\" style=\"width: 100px !important;\">"+
		     	""+proCategory+"</select></span><span id=\"proTaskSpan"+cnt+"\" style=\"float: left;\"><select name=\"proFolderTasks"+cnt+"\" id=\"proFolderTasks"+cnt+"\" style=\"width: 100px !important;\">"+
		     	""+proTasks+"</select></span>";
		     	
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"folderSharingType\" id=\"folderSharingType"+cnt+"\" style=\"width: 100px !important;\" class=\"validateRequired\" onchange=\"showHideResources(this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select>"
			    +"</span>"
			    +"<span id=\"proResourceSpan"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderEmployee"+cnt+"\" id=\"proFolderEmployee"+cnt+"\" style=\"width: 100px !important;\" multiple>"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+cnt+"\" onclick=\"showPoc('"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+cnt+"\" id=\"showPocType"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderPoc"+cnt+"\" id=\"proFolderPoc"+cnt+"\" style=\"width: 100px !important;\" multiple>"+proPoc+"</select></span>"
			    +"</div>";
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute("nowrap","nowrap");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.innerHTML = "<span id=\"isFolderEditSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderEdit\" id=\"isFolderEdit"+cnt+"\" value=\"1\">"+
		    	"<input type=\"checkbox\" name=\"folderEdit\" id=\"folderEdit"+cnt+"\" onclick=\"checkStatus(this, 'isFolderEdit"+cnt+"');\" checked/> Edit</span>"+
		    	"<span id=\"isFolderDeleteSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderDelete\" id=\"isFolderDelete"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"folderDelete\" id=\"folderDelete"+cnt+"\" onclick=\"checkStatus(this, 'isFolderDelete"+cnt+"');\"/> Delete</span>";
			
		    document.getElementById(rowCountName).value = cnt;
		    $("#proFolderEmployee"+cnt).multiselect().multiselectfilter();
		    $("#proFolderPoc"+cnt).multiselect().multiselectfilter();
		}
	
	function deleteFolder(count, tableName) {
		if(confirm('Are you sure, you want to delete this folder?')) {
			var trIndex = document.getElementById("folderTR"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	}
	
	
	function changeCategoryType(val, categorySpan, projectSpan) {
		if(val == '1') {
			document.getElementById(categorySpan).style.display = "none";
			document.getElementById(projectSpan).style.display = "block";
		} else if(val == '2') {
			document.getElementById(categorySpan).style.display = "block";
			document.getElementById(projectSpan).style.display = "none";
		}
	}
	
	
	function addNewSubFolder(folderTRId, rwIndex, tableName, rowCountName) {
			var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
			var val=(parseInt(rwIndex)+1);
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(val);
			
			row.id="SubFolderTR"+cnt;
		    var cell0 = row.insertCell(0);
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    cell0.innerHTML = "<span style=\"float:left; margin-left: 50px; margin-right: 9px;\"><input type=\"hidden\" name=\"SubFolderTR"+folderTRId+"\" id=\"SubFolderTR"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strSubFolderName"+folderTRId+"\" id=\"strSubFolderName"+cnt+"\" style=\"width: 200px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 4);\" value=\"Sub Folder Name\"/></span>"
		    +"<span style=\"float:left;\"><a href=\"javascript:void(0);\" onclick=\"addNewSubFolder('"+folderTRId+"', '"+rwIndex+"', '"+tableName+"' ,'"+rowCountName+"');\" class=\"fa fa-fw fa-plus\" title=\"Create New Folder\">&nbsp;</a>"
		    +"<a href=\"javascript:void(0)\" onclick=\"deleteSubFolder('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Folder\">&nbsp;</a></span>"
		    
	        +"<span style=\"float:left; width: 100%; margin-left: 50px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strSubFolderDescription"+folderTRId+"\" id=\"strSubFolderDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"
	        +"<a href=\"javascript:void(0);\" style=\"float:left; width:86%; margin: 4px 0px 15px 70px; \" onclick=\"addNewSubFolderDocs('"+cnt+"', this.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"');\"> +Add Document</a>";
		    
		    var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeSubFolder"+folderTRId+"\" id=\"proCategoryTypeSubFolder"+cnt+"\" style=\"width: 100px !important;\" onchange=\"changeCategoryType(this.value, 'proSubFolderCatagorySpan"+cnt+"', 'proSubFolderTaskSpan"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proSubFolderCatagorySpan"+cnt+"\" style=\"float: left; display: none\"><select name=\"proSubFolderCategory"+folderTRId+"_"+cnt+"\" id=\"proSubFolderTasksCategory"+cnt+"\" style=\"width: 100px !important;\">"+
		     	""+proCategory+"</select></span><span id=\"proSubFolderTaskSpan"+cnt+"\" style=\"float: left;\"><select name=\"proSubFolderTasks"+folderTRId+"_"+cnt+"\" id=\"proSubFolderTasks"+cnt+"\" style=\"width: 100px !important;\">"+
		     	""+proTasks+"</select></span>";
		    
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"SubfolderSharingType"+folderTRId+"\" id=\"SubfolderSharingType"+cnt+"\" style=\"width: 100px !important;\" class=\"validateRequired\" onchange=\"showHideResources(this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select>"
			    +"</span>"
			    +"<span id=\"proResourceSpan"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderEmployee"+folderTRId+"_"+cnt+"\" id=\"proSubFolderEmployee"+cnt+"\" style=\"width: 100px !important;\" multiple>"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+cnt+"\" onclick=\"showPoc('"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+cnt+"\" id=\"showPocType"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderPoc"+folderTRId+"_"+cnt+"\" id=\"proSubFolderPoc"+cnt+"\" style=\"width: 100px !important;\" multiple>"+proPoc+"</select></span>"
			    +"</div>";
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute("nowrap","nowrap");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.innerHTML = "<span id=\"isSubFolderEditSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderEdit"+folderTRId+"\" id=\"isSubFolderEdit"+cnt+"\" value=\"1\">"+
		    	"<input type=\"checkbox\" name=\"subFolderEdit"+folderTRId+"\" id=\"subFolderEdit"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderEdit"+cnt+"');\" checked/> Edit</span>"+
		    	"<span id=\"isSubFolderDeleteSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderDelete"+folderTRId+"\" id=\"isSubFolderDelete"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"subFolderDelete"+folderTRId+"\" id=\"subFolderDelete"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderDelete"+cnt+"');\"/> Delete</span>";
			
		    document.getElementById(rowCountName).value = cnt;
		    $("#proSubFolderEmployee"+cnt).multiselect().multiselectfilter();
		    $("#proSubFolderPoc"+cnt).multiselect().multiselectfilter();
		}
	
	function deleteSubFolder(count, tableName) {
		if(confirm('Are you sure, you want to delete this folder?')) {
			var trIndex = document.getElementById("SubFolderTR"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	}
	
	function addNewSubFolderDocs(folderTRId, rwIndex, tableName, rowCountName) {
		var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
			var val=(parseInt(rwIndex)+1);
			
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(val);
		    
		    row.id="SubfolderDocTR"+cnt;
		    var cell0 = row.insertCell(0);
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    /* cell0.innerHTML = "<span style=\"float:left; margin-left: 100px; margin-right: 9px;\"><input type=\"hidden\" name=\"SubfolderDocsTRId"+folderTRId+"\" id=\"folderDocsTRId"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strSubFolderScopeDoc"+folderTRId+"\" id=\"strSubFolderScopeDoc"+cnt+"\" style=\"width: 150px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
		    "<input type=\"file\" name=\"strSubFolderDoc"+folderTRId+"\" id=\"strSubFolderDoc"+cnt+"\" size=\"5\"/></span>"
	        +"<a href=\"javascript:void(0)\" onclick=\"addNewSubFolderDocs('"+folderTRId+"', this.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteSubFolderDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a>"
	        +"<span style=\"float:left; width: 100%; margin-left: 100px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strSubFolderDocDescription"+folderTRId+"\" id=\"strSubFolderDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"; */
	        
	        //===start parvez date: 13-09-2021=== 
	        cell0.innerHTML = "<span style=\"float:left; margin-left: 100px; margin-right: 9px;\"><input type=\"hidden\" name=\"SubfolderDocsTRId"+folderTRId+"\" id=\"folderDocsTRId"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strSubFolderScopeDoc"+folderTRId+"\" id=\"strSubFolderScopeDoc"+cnt+"\" style=\"width: 150px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
		    "<input type=\"file\" name=\"strSubFolderDoc"+folderTRId+"\" id=\"strSubFolderDoc"+cnt+"\" size=\"5\" required/></span>"
	        +"<a href=\"javascript:void(0)\" onclick=\"addNewSubFolderDocs('"+folderTRId+"', this.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteSubFolderDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a>"
	        +"<span style=\"float:left; width: 100%; margin-left: 100px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strSubFolderDocDescription"+folderTRId+"\" id=\"strSubFolderDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>";
		    //===end parvez date: 13-09-2021=== 
	        
		    var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeSubFolderDoc"+folderTRId+"\" id=\"proCategoryTypeSubFolderDoc"+cnt+"\" style=\"width: 100px !important;\" onchange=\"changeCategoryType(this.value, 'proSubFolderDocCatagorySpan"+cnt+"', 'proSubFolderDocTaskSpan"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proSubFolderDocCatagorySpan"+cnt+"\" style=\"float: left; display: none\"><select name=\"proSubFolderDocCategory"+folderTRId+"_"+cnt+"\" id=\"proSubFolderDocCategory"+cnt+"\" style=\"width: 100px !important;\">"+
		     	""+proCategory+"</select></span><span id=\"proSubFolderDocTaskSpan"+cnt+"\" style=\"float: left;\"><select name=\"proSubFolderDocTasks"+folderTRId+"_"+cnt+"\" id=\"proSubFolderDocTasks"+cnt+"\" style=\"width: 100px !important;\">"+
		     	""+proTasks+"</select></span>";
		     	
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"SubfolderDocDharingType"+folderTRId+"\" id=\"SubfolderDocDharingType"+cnt+"\" style=\"width: 100px !important;\" class=\"validateRequired\" onchange=\"showHideResources(this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select>"
			    +"</span>"
			    +"<span id=\"proResourceSpan"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderDocEmployee"+folderTRId+"_"+cnt+"\" id=\"proSubFolderDocEmployee"+cnt+"\" style=\"width: 100px !important;\" multiple>"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+cnt+"\" onclick=\"showPoc('"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+cnt+"\" id=\"showPocType"+cnt+"\" value=\"1\">"			    
			    +"<span id=\"proPocSpan"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proSubFolderDocPoc"+folderTRId+"_"+cnt+"\" id=\"proSubFolderDocPoc"+cnt+"\" style=\"width: 100px !important;\" multiple>"+proPoc+"</select></span>"
			    +"</div>";
		
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute("nowrap","nowrap");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.innerHTML = "<span id=\"isSubFolderDocEditSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderDocEdit"+folderTRId+"\" id=\"isSubFolderDocEdit"+cnt+"\" value=\"1\">"+
		    	"<input type=\"checkbox\" name=\"subFolderDocEdit"+folderTRId+"\" id=\"subFolderDocEdit"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderDocEdit"+cnt+"');\" checked/> Edit</span>"+
		    	"<span id=\"isSubFolderDocDeleteSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isSubFolderDocDelete"+folderTRId+"\" id=\"isSubFolderDocDelete"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"subFolderDocDelete"+folderTRId+"\" id=\"subFolderDocDelete"+cnt+"\" onclick=\"checkStatus(this, 'isSubFolderDocDelete"+cnt+"');\"/> Delete</span>";

		    document.getElementById(rowCountName).value = cnt;
		    $("#proSubFolderDocEmployee"+cnt).multiselect().multiselectfilter();
		    $("#proSubFolderDocPoc"+cnt).multiselect().multiselectfilter();
		}
	
	function deleteSubFolderDocs(count, tableName) {
		if(confirm('Are you sure, you want to delete this document?')) {
			var trIndex = document.getElementById("SubfolderDocTR"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	}
	
	
	function addNewDocs(tableName, rowCountName) { 
		var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
		    var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    
		    row.id="docTR"+cnt;
		    var cell0 = row.insertCell(0);
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    /* cell0.innerHTML = "<span style=\"float:left; margin-right: 9px;\"><input type=\"hidden\" name=\"docsTRId\" id=\"docsTRId"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strScopeDoc\" id=\"strScopeDoc"+cnt+"\" style=\"width: 150px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
		    "<input type=\"file\" name=\"strDoc\" id=\"strDoc"+cnt+"\" size=\"5\" /></span>"
	        +"<span style=\"float:left;\"><a href=\"javascript:void(0)\" onclick=\"addNewDocs('"+tableName+"', '"+rowCountName+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a></span>"
	        +"<span style=\"float:left; width: 100%; margin-top: 5px;\"><textarea rows=\"3\" name=\"strDocDescription\" id=\"strDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"; */
	        
	        //===start parvez date: 13-09-2021=== 
	        cell0.innerHTML = "<span style=\"float:left; margin-right: 9px;\"><input type=\"hidden\" name=\"docsTRId\" id=\"docsTRId"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strScopeDoc\" id=\"strScopeDoc"+cnt+"\" style=\"width: 150px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
		    "<input type=\"file\" name=\"strDoc\" id=\"strDoc"+cnt+"\" size=\"5\" required /></span>"
	        +"<span style=\"float:left;\"><a href=\"javascript:void(0)\" onclick=\"addNewDocs('"+tableName+"', '"+rowCountName+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a></span>"
	        +"<span style=\"float:left; width: 100%; margin-top: 5px;\"><textarea rows=\"3\" name=\"strDocDescription\" id=\"strDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>";
		    //===end parvez date: 13-09-2021=== 
	        
		    var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeDoc\" id=\"proCategoryTypeDoc"+cnt+"\" style=\"width: 100px !important;\" onchange=\"changeCategoryType(this.value, 'proDocCatagorySpan"+cnt+"', 'proDocTaskSpan"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proDocCatagorySpan"+cnt+"\" style=\"float: left; display: none\"><select name=\"proDocCategory"+cnt+"\" id=\"proDocCategory"+cnt+"\" style=\"width: 100px !important;\">"+
		     	""+proCategory+"</select></span><span id=\"proDocTaskSpan"+cnt+"\" style=\"float: left;\"><select name=\"proDocTasks"+cnt+"\" id=\"proDocTasks"+cnt+"\" style=\"width: 100px !important;\">"+
		     	""+proTasks+"</select></span>";
		     	
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"docSharingType\" id=\"docSharingType"+cnt+"\" style=\"width: 100px !important;\" class=\"validateRequired\" onchange=\"showHideResources(this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select>"
			    +"</span>"
			    +"<span id=\"proResourceSpan"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proDocEmployee"+cnt+"\" id=\"proDocEmployee"+cnt+"\" style=\"width: 100px !important;\" multiple>"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+cnt+"\" onclick=\"showPoc('"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+cnt+"\" id=\"showPocType"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proDocPoc"+cnt+"\" id=\"proDocPoc"+cnt+"\" style=\"width: 100px !important;\" multiple>"+proPoc+"</select></span>"
			    +"</div>";
			var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute("nowrap","nowrap");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.innerHTML = "<span id=\"isDocEditSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isDocEdit\" id=\"isDocEdit"+cnt+"\" value=\"1\">"+
		    	"<input type=\"checkbox\" name=\"docEdit\" id=\"docEdit"+cnt+"\" onclick=\"checkStatus(this, 'isDocEdit"+cnt+"');\" checked/> Edit</span>"+
		    	"<span id=\"isDocDeleteSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isDocDelete\" id=\"isDocDelete"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"docDelete\" id=\"docDelete"+cnt+"\" onclick=\"checkStatus(this, 'isDocDelete"+cnt+"');\"/> Delete</span>";

		    document.getElementById(rowCountName).value = cnt;
		    $("#proDocEmployee"+cnt).multiselect().multiselectfilter();
		    $("#proDocPoc"+cnt).multiselect().multiselectfilter();
		}
	
	function deleteDocs(count, tableName) {
		if(confirm('Are you sure, you want to delete this document?')) {
			var trIndex = document.getElementById("docTR"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	}
	
	
	function addNewFolderDocs(folderTRId, rwIndex, tableName, rowCountName) {
		var fdCnt = document.getElementById(rowCountName).value;
			var cnt=(parseInt(fdCnt)+1);
			var val=(parseInt(rwIndex)+1);

			var table = document.getElementById(tableName);
		    var rowCount = table.rows.length;
		    var row = table.insertRow(val);
		    
		    row.id="folderDocTR"+cnt;
		    var cell0 = row.insertCell(0);
		    cell0.setAttribute('style','width: 550px;');
		    cell0.setAttribute('class','tdBorderLine');
		    /* cell0.innerHTML = "<span style=\"float:left; margin-left: 50px; margin-right: 9px;\"><input type=\"hidden\" name=\"folderDocsTRId"+folderTRId+"\" id=\"folderDocsTRId"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strFolderScopeDoc"+folderTRId+"\" id=\"strFolderScopeDoc"+cnt+"\" style=\"width:150px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
		    "<input type=\"file\" name=\"strFolderDoc\" id=\"strFolderDoc"+cnt+"\" size=\"5\"/></span>"
	        +"<a href=\"javascript:void(0)\" onclick=\"addNewFolderDocs('"+folderTRId+"', this.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteFolderDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a>"
	        +"<span style=\"float:left; width: 100%; margin-left: 50px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strFolderDocDescription"+folderTRId+"\" id=\"strFolderDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>"; */
	        
	        //===start parvez date: 13-09-2021=== 
	        cell0.innerHTML = "<span style=\"float:left; margin-left: 50px; margin-right: 9px;\"><input type=\"hidden\" name=\"folderDocsTRId"+folderTRId+"\" id=\"folderDocsTRId"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"text\" name=\"strFolderScopeDoc"+folderTRId+"\" id=\"strFolderScopeDoc"+cnt+"\" style=\"width:150px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 3);\" value=\"Document Scope\"/> "+
		    "<input type=\"file\" name=\"strFolderDoc\" id=\"strFolderDoc"+cnt+"\" size=\"5\" required/></span>"
	        +"<a href=\"javascript:void(0)\" onclick=\"addNewFolderDocs('"+folderTRId+"', this.parentNode.parentNode.rowIndex, '"+tableName+"', '"+rowCountName+"')\" class=\"fa fa-fw fa-plus\" title=\"Add Document\">&nbsp;</a>"
	        +"<a href=\"javascript:void(0)\" onclick=\"deleteFolderDocs('"+cnt+"', '"+tableName+"')\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove Document\">&nbsp;</a>"
	        +"<span style=\"float:left; width: 100%; margin-left: 50px; margin-top: 5px;\"><textarea rows=\"3\" name=\"strFolderDocDescription"+folderTRId+"\" id=\"strFolderDocDescription"+cnt+"\" style=\"width: 330px !important; color: gray;\" onclick=\"clearData(this.id);\" onblur=\"fillData(this.id, 1);\">Description</textarea></span>";
		    //===end parvez date: 13-09-2021=== 
	        
		    var cell1 = row.insertCell(1);
		    cell1.setAttribute("valign","top");
		    cell1.setAttribute('class','tdBorderLine');
		    cell1.innerHTML = "<span style=\"float: left;\"><select name=\"proCategoryTypeFolderDoc"+folderTRId+"\" id=\"proCategoryTypeFolderDoc"+cnt+"\" style=\"width: 100px !important;\" onchange=\"changeCategoryType(this.value, 'proFolderDocCatagorySpan"+cnt+"', 'proFolderDocTaskSpan"+cnt+"');\">"+
		    "<option value=\"1\" selected>Project</option><option value=\"2\">Category</option></select></span>";
		    
		    var cell2 = row.insertCell(2);
		    cell2.setAttribute("valign","top");
		    cell2.setAttribute('class','tdBorderLine');
		    cell2.innerHTML = "<span id=\"proFolderDocCatagorySpan"+cnt+"\" style=\"float: left; display: none\"><select name=\"proFolderDocCategory"+folderTRId+"_"+cnt+"\" id=\"proFolderDocCategory"+cnt+"\" style=\"width: 100px !important;\">"+
		     	""+proCategory+"</select></span><span id=\"proFolderDocTaskSpan"+cnt+"\" style=\"float: left;\"><select name=\"proFolderDocTasks"+folderTRId+"_"+cnt+"\" id=\"proFolderDocTasks"+cnt+"\" style=\"width: 100px !important;\">"+
		     	""+proTasks+"</select></span>";
		     	
		    var cell3 = row.insertCell(3);
		    cell3.setAttribute("valign","top");
		    cell3.setAttribute('class','tdBorderLine');
		    cell3.innerHTML = "<div style=\"float: left; width: 100%;\"><span style=\"float: left;\"><select name=\"folderDocDharingType"+folderTRId+"\" id=\"folderDocDharingType"+cnt+"\" style=\"width: 100px !important;\" class=\"validateRequired\" onchange=\"showHideResources(this.value, '"+cnt+"')\">"
			    +"<option value=\"0\">Public</option><option value=\"1\">Private Team</option><option value=\"2\">Individual Resource</option></select>"
			    +"</span>"
			    +"<span id=\"proResourceSpan"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderDocEmployee"+folderTRId+"_"+cnt+"\" id=\"proFolderDocEmployee"+cnt+"\" style=\"width: 100px !important;\" multiple>"+proEmployee+"</select></span>"
			    +"</div>"
			    +"<div style=\"float: left; width: 100%; margin-top: 5px;\"><span style=\"float: left;\"><a href=\"javascript:void(0);\" style=\"font-weight:normal;\" id=\"sharePoc"+cnt+"\" onclick=\"showPoc('"+cnt+"')\">share customer</a></span>"
			    +"<input type=\"hidden\" name=\"showPocType"+cnt+"\" id=\"showPocType"+cnt+"\" value=\"1\">"
			    +"<span id=\"proPocSpan"+cnt+"\" style=\"display: none; float: left; margin-left: 9px;\"><select name=\"proFolderDocPoc"+folderTRId+"_"+cnt+"\" id=\"proFolderDocPoc"+cnt+"\" style=\"width: 100px !important;\" multiple>"+proPoc+"</select></span>"
			    +"</div>";
		    var cell4 = row.insertCell(4);
		    cell4.setAttribute("valign","top");
		    cell4.setAttribute("nowrap","nowrap");
		    cell4.setAttribute('class','tdBorderLine');
		    cell4.innerHTML = "<span id=\"isFolderDocEditSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderDocEdit"+folderTRId+"\" id=\"isFolderDocEdit"+cnt+"\" value=\"1\">"+
		    	"<input type=\"checkbox\" name=\"folderDocEdit"+folderTRId+"\" id=\"folderDocEdit"+cnt+"\" onclick=\"checkStatus(this, 'isFolderDocEdit"+cnt+"');\" checked/> Edit</span>"+
		    	"<span id=\"isFolderDocDeleteSpan"+cnt+"\" style=\"float: left; width: 100%;\"><input type=\"hidden\" name=\"isFolderDocDelete"+folderTRId+"\" id=\"isFolderDocDelete"+cnt+"\" value=\"0\">"+
		    	"<input type=\"checkbox\" name=\"folderDocDelete"+folderTRId+"\" id=\"folderDocDelete"+cnt+"\" onclick=\"checkStatus(this, 'isFolderDocDelete"+cnt+"');\"/> Delete</span>";

		    document.getElementById(rowCountName).value = cnt;
		    $("#proDocEmployee"+cnt).multiselect().multiselectfilter();
		    $("#proDocPoc"+cnt).multiselect().multiselectfilter();
		}
	
	
	function deleteFolderDocs(count, tableName) {
		if(confirm('Are you sure, you want to delete this document?')) {
			var trIndex = document.getElementById("folderDocTR"+count).rowIndex;
		    document.getElementById(tableName).deleteRow(trIndex);
		}
	}
	
	function showHideResources(val, count) {
		if(val == '2') {
			document.getElementById("proResourceSpan"+count).style.display = 'block';
		} else {
			document.getElementById("proResourceSpan"+count).style.display = 'none';
		}
	}
	
	function showPoc(count) {
		var val = document.getElementById("showPocType"+count).value;
		if(val == '1') {
			document.getElementById("proPocSpan"+count).style.display = 'block';
			document.getElementById("showPocType"+count).value = '0';
		} else {
			document.getElementById("proPocSpan"+count).style.display = 'none';
			document.getElementById("showPocType"+count).value = '1';
		}
	}
	
	function showTblHeader() {
		document.getElementById("folderTR0").style.display = 'table-row';
	}
	
	function viewVersionHistory(strProDocId,docStatusId,docVersionDivId,downSpanId,upSpanId,type,filePath,fileDir){
		var status = document.getElementById(docStatusId).value;
		if(status == '0') {
			document.getElementById(docStatusId).value = '1';
			document.getElementById(downSpanId).style.display = 'none';
			document.getElementById(upSpanId).style.display = 'block';
			
			document.getElementById(docVersionDivId).style.display = 'block';
	    	$('<img src=\"images1/ajax-loading-1.gif\"/>').appendTo("#"+docVersionDivId);
	    	var action = 'ProDocVersionHistory.action?proDocumentId='+ strProDocId+'&type='+type+'&filePath='+encodeURIComponent(filePath)+'&fileDir='+encodeURIComponent(fileDir);
	    	getContent(docVersionDivId, action);
			
		} else {
			document.getElementById(docStatusId).value = '0';
			document.getElementById(downSpanId).style.display = 'block';
			document.getElementById(upSpanId).style.display = 'none';
			
			document.getElementById(docVersionDivId).innerHTML = '';
			document.getElementById(docVersionDivId).style.display = 'none';
		}
	}
	
</script>
	<script type="text/javascript">
    function showLoading() {
        var div = document.createElement('div');
        var img = document.createElement('img');
        /* img.src = 'loading_bar.GIF'; */
        div.innerHTML = "Uploading...<br />";
        div.style.cssText = 'position: fixed; top: 50%; left: 40%; z-index: 5000; width: 222px; text-align: center; background: #EFEFEF; border: 1px solid #000';
        $(div).appendTo('body');
        return true;
    }
</script>

	<s:form id="frmAddProject_4" cssClass="formcss" action="PreAddNewProject1" name="frmAddProject_4" method="post" enctype="multipart/form-data" theme="simple" onsubmit="showLoading();">
		<div class="col-lg-12 col-md-12 col-sm-12">
			<s:hidden name="step" value="4"></s:hidden>
			<s:hidden name="operation"></s:hidden>
			<s:hidden name="pageType"></s:hidden>
			<s:hidden name="proType" id="proType"></s:hidden>
			<s:hidden name="pro_id"></s:hidden> 

			<!-- <table class="table_style" style="margin-left: 60px; width: 95%;"> -->
			<div style="overflow-x: auto;">
				<table class="table table_no_border">
					<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
					<tr>
						<td class="txtlabel alignRight" valign="top">Documents:</td>
						<td><a class="add_lvl" href="javascript:void(0);" onclick="addNewFolder('documentTable', 'folderDocscount'), showTblHeader();">Add Folder</a>
							<a class="add_lvl" href="javascript:void(0);" onclick="addNewDocs('documentTable', 'folderDocscount'), showTblHeader();">Add Document</a></td>
					</tr>
					<% } %>
					<tr>
						<td colspan="2">
							<table class="table table_no_border" id="documentTable" style="width: 100%;">
								<tr id="folderTR0" style="display: none;">
									<td style="width: 50%;"><input type="hidden" name="folderDocscount" id="folderDocscount" value="0" />Folder/Documents</td>
									<!-- <td style="width: 20%;">Scope Document</td> -->
									<td style="width: 12%;">Alignment Type</td>
									<td style="width: 12%;">Alignment</td>
									<td style="min-width: 20%;">Sharing</td>
									<td>Rights</td>
								</tr>
							</table>
							</td>
					</tr>

					<tr>
						<td colspan="2">
							<div style="float: left;">
								<% 	
								List<Map<String, String>> alProFolder = (List<Map<String, String>>) request.getAttribute("alProFolder");
								List<Map<String, String>> alMainDoc = (List<Map<String, String>>) request.getAttribute("alMainDoc");
								
								Map<String, List<Map<String,String>>> hmSubFolder = (Map<String, List<Map<String,String>>>)request.getAttribute("hmSubFolder");
								Map<String, List<Map<String,String>>> hmSubDoc = (Map<String, List<Map<String,String>>>)request.getAttribute("hmSubDoc");
								Map<String, String> hmFileIcon = (Map<String, String>) request.getAttribute("hmFileIcon");
								if(hmFileIcon == null) hmFileIcon = new HashMap<String, String>();
								
								String proDocMainPath = (String) request.getAttribute("proDocMainPath");
								String proDocRetrivePath = (String) request.getAttribute("proDocRetrivePath");
								String strOrgId = (String) request.getAttribute("strOrgId");
								if((alProFolder != null && !alProFolder.isEmpty()) || (alMainDoc !=null && !alMainDoc.isEmpty())) {
							%>
								<% 	
									for(int ii = 0; alProFolder != null && ii<alProFolder.size(); ii++) {
										Map<String, String> hmInnerFolder = (Map<String, String>) alProFolder.get(ii);
										List<Map<String, String>> alSubFolder = (List<Map<String, String>>) hmSubFolder.get(hmInnerFolder.get("PRO_DOCUMENT_ID")); 
										if(alSubFolder == null) alSubFolder = new ArrayList<Map<String,String>>();
										List<Map<String, String>> alFolderDoc = (List<Map<String, String>>) hmSubDoc.get(hmInnerFolder.get("PRO_DOCUMENT_ID")); 
										if(alFolderDoc == null) alFolderDoc = new ArrayList<Map<String,String>>();
										String fileCount ="-";
										int nFoldrCnt = (alSubFolder.size());
										int nFileCnt = (alFolderDoc.size());
										if(nFoldrCnt > 0 || nFileCnt > 0) {
											fileCount = (nFoldrCnt + nFileCnt)+"";
										}
										String folderSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME");
									%>
								<div id="folderTR_<%=ii %>" style="float: left;">
									<div style="float: left; width: 340px; margin-right: 9px;">
										<input type="hidden" name="hideFolder_<%=ii %>"
											id="hideFolder_<%=ii %>" value="0" />
										<div style="float: left; margin-right: 5px;">
											<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs('<%=ii %>','<%=alSubFolder.size()%>','<%=alFolderDoc.size()%>');">
												<%-- <img height="15" width="20" src="<%=request.getContextPath()%>/images1/icons/icons/folder_icon.png" /> --%>
												 <i class="fa fa-folder-open-o" style="font-size: 20px;height: 15px;"></i>
											</a>
										</div>
										<strong><%=hmInnerFolder.get("FOLDER_NAME") %></strong>
										<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=fileCount +" items" %></div>
										<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerFolder.get("DESCRIPTION") %></div>
										<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerFolder.get("ENTRY_DATE") %></div>
										<% if(uF.parseToInt(fileCount)>0) { %>
										<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
											<span style="float: left;"><%=fileCount +" items" %> view folders & files </span>
												<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs('<%=ii %>','<%=alSubFolder.size()%>','<%=alFolderDoc.size()%>');">
												<span id="FDDownarrowSpan<%=ii %>" style="float: left; margin-left: 2px; margin-top: 3px;">
													<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i></span>
												<span id="FDUparrowSpan<%=ii %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
													<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
												</a>
										</div>
										<% } %>
									</div>
									<div style="float: left; width: 120px; margin-right: 9px;">
										<div style="float: left; width: 100%;">Aligned with</div>
										<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerFolder.get("ALIGN") %></div>
									</div>
									<div style="float: left; width: 120px; margin-right: 9px;">
										<div style="float: left; width: 100%;">Shared with</div>
										<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerFolder.get("SHARING_TYPE") %></div>
									</div>
									<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
									<div style="float: left; width: 90px;">
										<div style="float: left; width: 100%;">Actions</div>
										<div style="float: left; width: 100%;">
											<select name="proCatAction<%=ii %>" id="proCatAction<%=ii %>" style="width: 80px !important;"
												onchange="executeFolderActions(this.value, '<%=hmInnerFolder.get("CLIENT_ID")%>','<%=hmInnerFolder.get("PRO_ID")%>','<%=hmInnerFolder.get("FOLDER_NAME") %>','<%=hmInnerFolder.get("PRO_DOCUMENT_ID") %>', 'F', '', '', 'folderTR_<%=ii %>', '<%=folderSavePath %>');">
												<option value="">Actions</option>
												<option value="1">Edit</option>
												<option value="2">Delete</option>
											</select>
										</div>
									</div>
									<% } %>
									<%
										for(int j = 0; j < alSubFolder.size(); j++) {
											Map<String, String> hmInnerSubFolder = (Map<String, String>) alSubFolder.get(j);
											List<Map<String, String>> alDoc = (List<Map<String, String>>) hmSubDoc.get(hmInnerSubFolder.get("PRO_DOCUMENT_ID")); 
											if(alDoc == null) alDoc = new ArrayList<Map<String,String>>();
											
											String subFolderSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerSubFolder.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");
											String fileSubCount ="-";
											if(alDoc !=null && alDoc.size() > 0) {
												fileSubCount = alDoc.size()+"";
											}
										%>
									<div id="folderTR_<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;">

										<div style="float: left; margin-left: 23px; width: 317px; margin-right: 9px;">
											<input type="hidden" name="hideFolder_<%=ii %>_<%=j %>" id="hideFolder_<%=ii %>_<%=j %>" value="0" />
											<div style="float: left; margin-right: 5px;">
												<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs1('<%=ii %>','<%=j %>','<%=alDoc.size() %>');">
													<%-- <img height="15" width="20" src="<%=request.getContextPath()%>/images1/icons/icons/folder_icon.png" /> --%>
													<i class="fa fa-folder-open-o" style="font-size: 20px;height: 15px;"></i>

												</a>
											</div>

											<strong><%=hmInnerSubFolder.get("FOLDER_NAME") %></strong>
											<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=fileSubCount +" items" %></div>
											<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubFolder.get("DESCRIPTION") %></div>
											<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubFolder.get("ENTRY_DATE") %></div>
											<% if(uF.parseToInt(fileSubCount)>0) { %>
											<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
												<span style="float: left;"><%=fileSubCount +" items" %> view files </span> 
													<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="openCloseDocs1('<%=ii %>','<%=j %>','<%=alDoc.size() %>');">
													<span id="FDDownarrowSpan<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px;">
														<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i></span>
													<span id="FDUparrowSpan<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
														<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i></span>
													</a>
											</div>
											<% } %>
										</div>
										<div style="float: left; width: 120px; margin-right: 9px;">
											<div style="float: left; width: 100%;">Aligned with</div>
											<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubFolder.get("ALIGN") %></div>
										</div>
										<div style="float: left; width: 120px; margin-right: 9px;">
											<div style="float: left; width: 100%;">Shared with</div>
											<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubFolder.get("SHARING_TYPE") %></div>
										</div>
										<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
										<div style="float: left; width: 90px;">
											<div style="float: left; width: 100%;">Actions</div>
											<div style="float: left; width: 100%;">
												<select name="proCatAction<%=ii %>_<%=j %>" id="proCatAction<%=ii %>_<%=j %>" style="width: 80px !important;"
													onchange="executeFolderActions(this.value, '<%=hmInnerSubFolder.get("CLIENT_ID")%>','<%=hmInnerSubFolder.get("PRO_ID")%>','<%=hmInnerSubFolder.get("FOLDER_NAME") %>','<%=hmInnerSubFolder.get("PRO_DOCUMENT_ID") %>', 'SF', '', '', 'folderTR_<%=ii %>_<%=j %>', '<%=subFolderSavePath %>');">
													<option value="">Actions</option>
													<option value="1">Edit</option>
													<option value="2">Delete</option>
												</select>
											</div>
										</div>
										<% } %>

										<%
											for(int k = 0; k<alDoc.size(); k++){
												Map<String, String> hmInnerSubDoc = (Map<String, String>) alDoc.get(k);
												String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerSubDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");
												String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerSubDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME");
												
												String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerSubDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerSubFolder.get("FOLDER_NAME")+"/"+hmInnerSubDoc.get("DOCUMENT_NAME");;
												
												String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
												if(hmFileIcon.containsKey(hmInnerSubDoc.get("FILE_EXTENSION"))){
													fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerSubDoc.get("FILE_EXTENSION"));
												}
												String action = "ProjectDocumentFact.action?clientId="+hmInnerSubDoc.get("CLIENT_ID")+"&proId="+hmInnerSubDoc.get("PRO_ID")+"&folderName="+hmInnerSubDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerSubDoc.get("PRO_DOCUMENT_ID")+"&type=2&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
												int nDocVersion = uF.parseToInt(hmInnerSubDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerSubDoc.get("DOC_VERSION")) : 1;
												%>
										<div id="folderTR_<%=ii %>_<%=j %>_<%=k %>" style="float: left; width: 100%; margin-top: 10px; display: none;">
											<div style="float: left; margin-left: 48px; width: 292px; margin-right: 9px;">
												<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document">
													<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>
												</a>
												<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("FILE_SIZE") %></div>
												<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("DESCRIPTION") %></div>
												<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerSubDoc.get("ENTRY_DATE") %></div>
												<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
													<span style="float: left;"> <a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">View File</a> |</span>
													<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a></span>
												</div>

											</div>
											<div style="float: left; width: 120px; margin-right: 9px;">
												<div style="float: left; width: 100%;">
													Version <strong><%=nDocVersion %></strong>
												</div>
												<div style="float: left; width: 100%; margin-top: -5px;">
													<%if(nDocVersion > 1){ %>
													<span style="float: left;">Version history </span>
													<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus<%=ii %>_<%=j %>_<%=k %>" value="0" />
													<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>,'proDocsVersionDiv<%=ii %>_<%=j %>_<%=k %>','proDocsVersionDiv<%=ii %>_<%=j %>_<%=k %>','VHDownarrowSpan<%=ii %>_<%=j %>_<%=k %>', 'VHUparrowSpan<%=ii %>_<%=j %>_<%=k %>','2','<%=filePath1%>','<%=fileDir %>')">
														<span id="VHDownarrowSpan<%=ii %>_<%=j %>_<%=k %>" style="float: left; margin-left: 2px; margin-top: 3px;">
															<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> </span>
														<span id="VHUparrowSpan<%=ii %>_<%=j %>_<%=k %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
															<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i> </span>
													</a>
													<%} %>
												</div>
											</div>
											<div style="float: left; width: 120px; margin-right: 9px;">
												<div style="float: left; width: 100%;">Aligned with</div>
												<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubDoc.get("ALIGN") %></div>
											</div>
											<div style="float: left; width: 120px; margin-right: 9px;">
												<div style="float: left; width: 100%;">Shared with</div>
												<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerSubDoc.get("SHARING_TYPE") %></div>
											</div>
											<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
											<div style="float: left; width: 90px;">
												<div style="float: left; width: 100%;">Actions</div>
												<div style="float: left; width: 100%;">
													<select name="proCatAction<%=ii %>_<%=j %>_<%=k %>" id="proCatAction<%=ii %>_<%=j %>_<%=k %>" style="width: 80px !important;"
														onchange="executeFolderActions(this.value, '<%=hmInnerSubDoc.get("CLIENT_ID")%>','<%=hmInnerSubDoc.get("PRO_ID")%>','<%=hmInnerSubDoc.get("DOCUMENT_NAME") %>','<%=hmInnerSubDoc.get("PRO_DOCUMENT_ID") %>', 'SFD', '<%=filePath1 %>', '<%=fileDir %>', 'folderTR_<%=ii %>_<%=j %>_<%=k %>', '<%=fileSavePath %>');">
														<option value="">Actions</option>
														<option value="1">Edit</option>
														<option value="2">Delete</option>
													</select>
												</div>
											</div>
											<% } %>
										</div>
										<div id="proDocsVersionDiv<%=ii %>_<%=j %>_<%=k %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
										<%} %>
									</div>
									<%} %>
									<%
										for(int j = 0; j < alFolderDoc.size();j++){
											Map<String, String> hmInnerDoc = (Map<String, String>) alFolderDoc.get(j);
											String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
											String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME");
											
											String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerFolder.get("FOLDER_NAME")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
											
											String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
											if(hmFileIcon.containsKey(hmInnerDoc.get("FILE_EXTENSION"))){
												fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerDoc.get("FILE_EXTENSION"));
											}
											String action = "ProjectDocumentFact.action?clientId="+hmInnerDoc.get("CLIENT_ID")+"&proId="+hmInnerDoc.get("PRO_ID")+"&folderName="+hmInnerDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerDoc.get("PRO_DOCUMENT_ID")+"&type=3&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
											int nDocVersion = uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) : 1;
										%>
									<div id="docFolderTR_<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;">
										<div style="float: left; margin-left: 23px; width: 317px; margin-right: 9px;">
											<%-- <a href="javascript:void(0)" style="font-weight: normal; color: black;"onclick="projectDocFact('<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>',3,'<%=filePath1 %>','<%=fileDir %>');" title="View Document"> --%>
											<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
											<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerDoc.get("DOCUMENT_NAME") %>
											</a>
											<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("FILE_SIZE") %></div>
											<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("DESCRIPTION") %></div>
											<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("ENTRY_DATE") %></div>
											<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
												<span style="float: left;"> <a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">View File</a> |</span>
												<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a> </span>
											</div>
										</div>
										<div style="float: left; width: 120px; margin-right: 9px;">
											<div style="float: left; width: 100%;">
												Version <strong><%=nDocVersion %></strong>
											</div>
											<div style="float: left; width: 100%; margin-top: -5px;">
												<%if(nDocVersion > 1){ %>
												<span style="float: left;">Version history </span> 
												<input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus<%=ii %>_<%=j %>" value="0" />
												<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus<%=ii %>_<%=j %>','proDocsVersionDiv<%=ii %>_<%=j %>','VHDownarrowSpan<%=ii %>_<%=j %>', 'VHUparrowSpan<%=ii %>_<%=j %>','3','<%=filePath1%>','<%=fileDir %>')">
													<span id="VHDownarrowSpan<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px;">
														<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> </span> 
													<span id="VHUparrowSpan<%=ii %>_<%=j %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
														<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i></span> 
												</a>
												<%} %>
											</div>
										</div>
										<div style="float: left; width: 120px; margin-right: 9px;">
											<div style="float: left; width: 100%;">Aligned with</div>
											<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerDoc.get("ALIGN") %></div>
										</div>
										<div style="float: left; width: 120px; margin-right: 9px;">
											<div style="float: left; width: 100%;">Shared with</div>
											<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerDoc.get("SHARING_TYPE") %></div>
										</div>
										<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
										<div style="float: left; width: 90px;">
											<div style="float: left; width: 100%;">Actions</div>
											<div style="float: left; width: 100%;">
												<select name="proCatFDocAction<%=ii %>_<%=j %>" id="proCatFDocAction<%=ii %>_<%=j %>" style="width: 80px !important;"
													onchange="executeFolderActions(this.value, '<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>', 'FD', '<%=filePath1 %>', '<%=fileDir %>', 'docFolderTR_<%=ii %>_<%=j %>', '<%=fileSavePath %>');">
													<option value="">Actions</option>
													<option value="1">Edit</option>
													<option value="2">Delete</option>
												</select>
											</div>
										</div>
										<% } %>
									</div>
									<div id="proDocsVersionDiv<%=ii %>_<%=j %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
									<%} %>

								</div>
								<%} %>

								<% 	
								for(int i = 0; alMainDoc !=null && i<alMainDoc.size(); i++) {
									Map<String, String> hmInnerDoc = (Map<String, String>) alMainDoc.get(i);
									String filePath1 = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
									String fileDir = proDocRetrivePath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID");
									
									String fileSavePath = proDocMainPath+strOrgId+"/Projects/"+hmInnerDoc.get("PRO_ID")+"/"+hmInnerDoc.get("DOCUMENT_NAME");
									
									String fileIcon = request.getContextPath()+"/images1/icons/icons/file_icon.png";
									if(hmFileIcon.containsKey(hmInnerDoc.get("FILE_EXTENSION"))){
										fileIcon = request.getContextPath()+"/images1/file_icon/"+hmFileIcon.get(hmInnerDoc.get("FILE_EXTENSION"));
									}
									String action = "ProjectDocumentFact.action?clientId="+hmInnerDoc.get("CLIENT_ID")+"&proId="+hmInnerDoc.get("PRO_ID")+"&folderName="+hmInnerDoc.get("DOCUMENT_NAME")+"&proFolderId="+hmInnerDoc.get("PRO_DOCUMENT_ID")+"&type=3&filePath="+URLEncoder.encode(filePath1)+"&fileDir="+URLEncoder.encode(fileDir);
									int nDocVersion = uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) > 0 ? uF.parseToInt(hmInnerDoc.get("DOC_VERSION")) : 1;
								%>
								<div id="docTR_<%=i %>" style="float: left; margin-top: 10px;">
									<div style="float: left; width: 240px; margin-right: 9px;">
										<a target="_blank" href="<%=action %>" style="font-weight: normal; color: black;" title="View Document"> 
										<img height="18" width="18" src="<%=fileIcon %>" />&nbsp;<%=hmInnerDoc.get("DOCUMENT_NAME") %>
										</a>
										<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("FILE_SIZE") %></div>
										<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("DESCRIPTION") %></div>
										<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;"><%=hmInnerDoc.get("ENTRY_DATE") %></div>
										<div style="float: left; color: gray; width: 90%; margin-top: -5px; margin-left: 25px;">
											<span style="float: left;"> <a target="_blank" href="<%=action %>" style="font-weight: normal;" title="View Document">View File</a> |</span> 
											<span style="float: left; margin-left: 2px;"><a href="<%=filePath1 %>" style="font-weight: normal;">Download</a> </span>
										</div>

									</div>
									<div style="float: left; width: 120px; margin-right: 9px;">
										<div style="float: left; width: 100%;">
											Version <strong><%=nDocVersion %></strong>
										</div>
										<div style="float: left; width: 100%; margin-top: -5px;">
											<%if(nDocVersion > 1){ %>
											<span style="float: left;">Version history </span> <input type="hidden" name="proDocsSpanStatus" id="proDocsSpanStatus<%=i %>" value="0" /> 
											<a href="javascript:void(0);" style="font-weight: normal; color: black;" onclick="viewVersionHistory(<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>,'proDocsSpanStatus<%=i %>','proDocsVersionDiv<%=i %>','VHDownarrowSpan<%=i %>', 'VHUparrowSpan<%=i %>','3','<%=filePath1%>','<%=fileDir %>')">
												<span id="VHDownarrowSpan<%=i %>" style="float: left; margin-left: 2px; margin-top: 3px;">
													 <i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i></span> 
												<span id="VHUparrowSpan<%=i %>" style="float: left; margin-left: 2px; margin-top: 3px; display: none;">
													<i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i> </span> 
											</a>
											<%} %>
										</div>
									</div>
									<div style="float: left; width: 120px; margin-right: 9px;">
										<div style="float: left; width: 100%;">Aligned with</div>
										<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerDoc.get("ALIGN") %></div>
									</div>
									<div style="float: left; width: 120px;">
										<div style="float: left; width: 100%; margin-right: 9px;">Shared with</div>
										<div style="float: left; width: 100%; margin-top: -5px;"><%=hmInnerDoc.get("SHARING_TYPE") %></div>
									</div>
									<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
									<div style="float: left; width: 90px;">
										<div style="float: left; width: 100%;">Actions</div>
										<div style="float: left; width: 100%;">
											<select name="proCatDocAction<%=i %>" id="proCatDocAction<%=i %>" style="width: 80px !important;"
												onchange="executeFolderActions(this.value, '<%=hmInnerDoc.get("CLIENT_ID")%>','<%=hmInnerDoc.get("PRO_ID")%>','<%=hmInnerDoc.get("DOCUMENT_NAME") %>','<%=hmInnerDoc.get("PRO_DOCUMENT_ID") %>', 'D', '<%=filePath1 %>', '<%=fileDir %>', 'docTR_<%=i %>', '<%=fileSavePath %>');">
												<option value="">Actions</option>
												<option value="1">Edit</option>
												<option value="2">Delete</option>
											</select>
										</div>
									</div>
									<% } %>
								</div>
								<div id="proDocsVersionDiv<%=i %>" style="float: left; width: 100%; margin-top: 10px; display: none;"></div>
								<% } %>
								<%} else {%>
								<div style="float: left; width: 1050px;" class="nodata msg">
									<span>No documents attached.</span>
								</div>
								<%} %>
							</div></td>
					</tr>

				</table>
			</div>
		</div>


		<div class="clr"></div>
		<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
		<div style="margin: 0px 0px 0px 120px">
			<table id="submitBtnTable_4" border="0" class="table table_no_border">
				<tr>
					<td>
						<% if(strProOwnerOrTL == null || !strProOwnerOrTL.equals("2")) { %>
						<% if(operation != null && operation.equals("E")) { %> 
							<input type="button" name="skipProcced" value="Skip & Proceed" class="btn btn-primary" style="float: right; margin-right: 5px;" onclick="skipAndProcced('<%=request.getAttribute("pro_id") %>','4', '<%=pageType %>');" />
						<% } %>
						<input type="submit" name="submit" value="Submit & Proceed" class="btn btn-primary" style="float: right; margin-right: 5px;" />
						<!-- onclick="submitAndProcced('frmAddProject_4', '4');" --> <% } %>
						<input type="submit" name="stepSave" value="Save & Exit" class="btn btn-primary" style="float: right; margin-right: 5px;" />
						<!-- onclick="saveAndExit('frmAddProject_4');" --> 
						<input type="button" value="Cancel" class="btn btn-danger" style="float: right; margin-right: 5px;" name="cancel" onclick="closeForm('<%=pageType %>');"></td>
					<td></td>
				</tr>
			</table>
		</div>
		<% } %>
	</s:form>
</s:if>


<s:if test="step==6">

	<script>
	$(function() {
		$("#strDueDate").datepicker({format : 'dd/mm/yyyy'});
	});
		
	jQuery(document).ready(function() {
		//jQuery("#formID_6").validationEngine();
		getExchangeValue();
	
	});


	function checkCosting(id) {
		
		if (document.getElementById(id).value == 'M') {
			document.getElementById("idIdealTime").innerHTML = "Estimated man-months";
		} else if (document.getElementById(id).value == 'H') {
			document.getElementById("idIdealTime").innerHTML = "Estimated man-hours";
		} else {
			document.getElementById("idIdealTime").innerHTML = "Estimated man-days";
		}
	}
	 
	
function checkBillingType(id, type) {
		
		if (document.getElementById(id).value == 'F') {
			document.getElementById("idFBillingAmount").style.display = 'table-row';
			document.getElementById("idFActualBilling").style.display = 'table-row';
			document.getElementById("DailyTR").style.display = 'none';
			checkCosting('idBasisCosting');
			
		} else if (document.getElementById(id).value == 'M') {
			document.getElementById("idFBillingAmount").style.display = 'none';
			document.getElementById("idFActualBilling").style.display = 'none';
			document.getElementById("DailyTR").style.display = 'none';
			document.getElementById("idIdealTime").innerHTML = "Estimated man-months";
			
		} else if (document.getElementById(id).value == 'H') {
			document.getElementById("idFBillingAmount").style.display = 'none';
			document.getElementById("idFActualBilling").style.display = 'none';
			document.getElementById("DailyTR").style.display = 'none';
			document.getElementById("idIdealTime").innerHTML = "Estimated man-hours";
			
		} else if (document.getElementById(id).value == 'D') {
			document.getElementById("idFBillingAmount").style.display = 'none';
			document.getElementById("idFActualBilling").style.display = 'none';
			document.getElementById("DailyTR").style.display = 'table-row';
			document.getElementById("idIdealTime").innerHTML = "Estimated man-days";
			
		} else {
			document.getElementById("idFBillingAmount").style.display = 'none';
			document.getElementById("idFActualBilling").style.display = 'none';
			document.getElementById("DailyTR").style.display = 'none';
			document.getElementById("idIdealTime").innerHTML = "Estimated man-days";
		}
	
	}
	
	
	function hideShowMilestone(value, type) {
		var idBilling = document.getElementById("idBilling").value;
		if(idBilling == 'F') {
			if(value == 'O') {
				document.getElementById("dependencyTR").style.display = 'table-row';
				document.getElementById("milestoneDiv").style.display = 'block';
				
			} else {
				document.getElementById("dependencyTR").style.display = 'none';
				document.getElementById("milestoneDiv").style.display = 'none';
			}
		}
		getBillingCycle(value, type);
	}
	
	
	function getBillingCycle(billingFreq, type) {
		
		if(billingFreq == 'W') {
			document.getElementById("billingCycle").style.display="block";
			document.getElementById("weekly").style.display="block";
			if(type == '') {
				document.getElementById("weekdayCycle").selectedIndex = "0";
				document.getElementById("dayCycle").selectedIndex = "0";
			}
			document.getElementById("monthly").style.display="none";
		} else if(billingFreq == 'B') {
			document.getElementById("billingCycle").style.display="block";
			document.getElementById("monthly").style.display="block";
			if(type == '') {
				document.getElementById("weekdayCycle").selectedIndex = "0";
				document.getElementById("dayCycle").selectedIndex = "0";
			}
			document.getElementById("weekly").style.display="none";
		} else if(billingFreq == 'M' || billingFreq == 'Q') {
			document.getElementById("billingCycle").style.display="block";
			document.getElementById("monthly").style.display="block";
			if(type == '') {
				document.getElementById("weekdayCycle").selectedIndex = "0";
				document.getElementById("dayCycle").selectedIndex = "0";
			}
			document.getElementById("weekly").style.display="none";
		} else {
			if(type == '') {
				document.getElementById("weekdayCycle").selectedIndex = "0";
				document.getElementById("dayCycle").selectedIndex = "0";
			}
			document.getElementById("billingCycle").style.display="none";
			document.getElementById("weekly").style.display="none";
			document.getElementById("monthly").style.display="none";
		}
	}
	
	
	function showMilestoneData(val) {
		var existMilestones = document.getElementById("hideMilestones").value;
		if(val == '1') {
			document.getElementById("milestoneTaskLabel").innerHTML = "Milestone %";
			document.getElementById("milestoneTotPercentSpan").style.display = 'block';
		} else if(val == '2') {
			document.getElementById("milestoneTaskLabel").innerHTML = "Task";
			document.getElementById("milestoneTotPercentSpan").style.display = 'none';
		} else {
			document.getElementById("milestoneTaskLabel").innerHTML = "Milestone %/ Task";
			document.getElementById("milestoneTotPercentSpan").style.display = 'none';
		}
		for(var i=0; i<existMilestones; i++) {
			if(val == '1') {
				if(document.getElementById("milestonePercentSpan"+i))
					document.getElementById("milestonePercentSpan"+i).style.display = 'block';
				if(document.getElementById("milestonePercent"+i))
					document.getElementById("milestonePercent"+i).value = '';
				if(document.getElementById("milestoneAmount"+i))
					document.getElementById("milestoneAmount"+i).value = '';
				
				if(document.getElementById("taskSpan"+i)) {
					if(document.getElementById("projectTask"+i))
						document.getElementById("projectTask"+i).selectedIndex = '0';
					document.getElementById("taskSpan"+i).style.display = 'none';
				}	
			} else if(val == '2') {
				if(document.getElementById("milestonePercentSpan"+i)) {
					if(document.getElementById("milestonePercent"+i))
						document.getElementById("milestonePercent"+i).value = '';
					if(document.getElementById("milestoneAmount"+i))
						document.getElementById("milestoneAmount"+i).value = '';
					document.getElementById("milestonePercentSpan"+i).style.display = 'none';
				}	
				if(document.getElementById("taskSpan"+i))
					document.getElementById("taskSpan"+i).style.display = 'block';
			}  else {
				if(document.getElementById("milestonePercentSpan"+i)) {
					document.getElementById("milestonePercentSpan"+i).style.display = 'none';
				}
				if(document.getElementById("milestonePercent"+i))
					document.getElementById("milestonePercent"+i).value = '';
				if(document.getElementById("milestoneAmount"+i))
					document.getElementById("milestoneAmount"+i).value = '';
				
				if(document.getElementById("taskSpan"+i)) {
					if(document.getElementById("projectTask"+i))
						document.getElementById("projectTask"+i).selectedIndex = '0';
					document.getElementById("taskSpan"+i).style.display = 'none';
				}	
			}
		}
	}
	
	
	function showMilestoneDataOnload(val) {
		var existMilestones = document.getElementById("hideMilestones").value;
		if(val == '1') {
			document.getElementById("milestoneTaskLabel").innerHTML = "Milestone %";
			document.getElementById("milestoneTotPercentSpan").style.display = 'block';
		} else if(val == '2') {
			document.getElementById("milestoneTaskLabel").innerHTML = "Task";
			document.getElementById("milestoneTotPercentSpan").style.display = 'none';
		} else {
			document.getElementById("milestoneTaskLabel").innerHTML = "Milestone %/ Task";
			document.getElementById("milestoneTotPercentSpan").style.display = 'none';
		}
		for(var i=0; i<existMilestones; i++) {
			if(val == '1') {
				if(document.getElementById("milestonePercentSpan"+i))
					document.getElementById("milestonePercentSpan"+i).style.display = 'block';
				if(document.getElementById("taskSpan"+i)) {
					document.getElementById("taskSpan"+i).style.display = 'none';
				}	
			} else if(val == '2') {
				if(document.getElementById("milestonePercentSpan"+i)) {
					document.getElementById("milestonePercentSpan"+i).style.display = 'none';
				}
				if(document.getElementById("taskSpan"+i))
					document.getElementById("taskSpan"+i).style.display = 'block';
			}  else {
				if(document.getElementById("milestonePercentSpan"+i)) {
					document.getElementById("milestonePercentSpan"+i).style.display = 'none';
				}
				
				if(document.getElementById("taskSpan"+i)) {
					document.getElementById("taskSpan"+i).style.display = 'none';
				}	
			}
		}
	}
	
	
	var empTaskList = '<%=(String)request.getAttribute("sbOption") %>';
	var milestoneCnt = 0;
	function addMilestone() {
		milestoneCnt = document.getElementById("hideExsitMilestones").value;
		milestoneCnt++;
		var dependentOn = document.getElementById("milestoneDependentOn").value;
		var taskSpn = "none";
		var milestonePercentSpn = "none";
		
		if(dependentOn == '1') {
			taskSpn = "none";
			milestonePercentSpn = "block";
		} else if(dependentOn == '2') {
			taskSpn = "block";
			milestonePercentSpn = "none";
		}
		var table = document.getElementById("milestoneHeadTable");
	    var rowCount = table.rows.length;
	    var val=(parseInt(rowCount)-1);
	    var row = table.insertRow(val);
	    row.id="milestoneTR_"+milestoneCnt;
	    var cell0 = row.insertCell(0);
	    cell0.innerHTML = "<input type=\"text\" name=\"milestoneName\" id=\"milestoneName"+milestoneCnt+"\"/>";
	    var cell1 = row.insertCell(1);
	    cell1.innerHTML = "<textarea rows=\"2\" cols=\"25\" name=\"milestoneDescription\" id=\"milestoneDescription"+milestoneCnt+"\"></textarea>";
	    var cell2 = row.insertCell(2);
	    cell2.innerHTML = "<span id=\"taskSpan"+milestoneCnt+"\" style=\"display: "+taskSpn+";\"><select name=\"projectTask\" id=\"projectTask"+milestoneCnt+"\" style=\"width: 150px !important; height: 27px;\">"
	    	+"<option value=\"\">Select Task</option>"+empTaskList+"</select></span><span id=\"milestonePercentSpan"+milestoneCnt+"\" style=\"display: "+milestonePercentSpn+";\">"
	    	+"<input type=\"text\" name=\"milestonePercent\" id=\"milestonePercent"+milestoneCnt+"\" style=\"width: 110px !important;\" onkeyup=\"calculateMilestoneAmount('P','"+milestoneCnt+"')\" onchange=\"calculateMilestoneAmount('P','"+milestoneCnt+"')\" onkeypress=\"return isNumberKey(event)\" /></span>";
		var cell3 = row.insertCell(3);
		cell3.innerHTML = "<input type=\"text\" name=\"milestoneAmount\" id=\"milestoneAmount"+milestoneCnt+"\" style=\"width: 110px !important;\" onkeyup=\"calculateMilestoneAmount('A','"+milestoneCnt+"')\" onchange=\"calculateMilestoneAmount('A','"+milestoneCnt+"')\" onkeypress=\"return isNumberKey(event)\" />";
		var cell5 = row.insertCell(4);
		cell5.innerHTML = "<a href=\"javascript:void(0)\" onclick=\"addMilestone()\" class=\"fa fa-fw fa-plus\" title=\"Add\">&nbsp;</a>"+
    		"<a href=\"javascript:void(0)\" style=\"float: left;\" onclick=\"deleteMilestone(this.id)\" id=\""+milestoneCnt+"\" class=\"fa fa-fw fa-remove\" title=\"Remove\">&nbsp;</a>";

    	var existMilestonesCnt = document.getElementById("hideMilestones").value;
   	    existMilestonesCnt++;
   	    document.getElementById("hideMilestones").value = existMilestonesCnt; 
   	    document.getElementById("hideExsitMilestones").value = milestoneCnt;
		
	}
	
	
	function deleteMilestone(count) {
		if(confirm('Are you sure, you want to delete this milestone?')) {
			var trIndex = document.getElementById("milestoneTR_"+count).rowIndex;
			document.getElementById("milestoneHeadTable").deleteRow(trIndex);
		}
	}

	function calculateMilestoneAmount(type, cnt) {
		
		var fixedAmt = document.getElementById("billingAmountF").value;
		var milestonePer = document.getElementById("milestonePercent"+cnt).value;
		var milestoneAmt = document.getElementById("milestoneAmount"+cnt).value;
		if(type == 'P') {
			
			var existMilestones = document.getElementById("hideMilestones").value;
			var totMilestoneAmt = 0;
			for(var i=0; i<existMilestones; i++) {
				if(document.getElementById("milestoneAmount"+i)) {
					var mAmt = 0;
					if(document.getElementById("milestoneAmount"+i).value != '') {
						mAmt = document.getElementById("milestoneAmount"+i).value;
					}
					totMilestoneAmt = parseFloat(totMilestoneAmt) + parseFloat(mAmt);
				}
			}
			
			if(parseFloat(milestonePer) > 100) {
				alert("Percentage more than 100");
				document.getElementById("milestonePercent"+cnt).value = "";
				document.getElementById("milestoneAmount"+cnt).value = "";
			} else {
				var mlStoneAmt = 0;
				if(parseFloat(fixedAmt) > 0) {
					if(milestonePer == '') {
						milestonePer = 0;
					}
					var mStoneAmt = (parseFloat(fixedAmt) * parseFloat(milestonePer)) /100;
					mlStoneAmt = parseFloat(mStoneAmt) - parseFloat(totMilestoneAmt);
					if(parseFloat(mlStoneAmt) < 0) {
						mlStoneAmt = 0;
					}
				} else {
					document.getElementById("milestonePercent"+cnt).value = '';
				}
				document.getElementById("milestoneAmount"+cnt).value = mlStoneAmt.toFixed(1);
			}
		} else if(type == 'A') {
			var existMilestones = document.getElementById("hideMilestones").value;
			var totMilestoneAmount = 0;
			for(var i=0; i<existMilestones; i++) {
				if(document.getElementById("milestoneAmount"+i)){
					var mAmount = 0;
					if(document.getElementById("milestoneAmount"+i).value != '') {
						mAmount = document.getElementById("milestoneAmount"+i).value;
					}
					totMilestoneAmount = parseFloat(totMilestoneAmount) + parseFloat(mAmount);
				}
			}
			if(parseFloat(totMilestoneAmount) > parseFloat(fixedAmt)) {
				alert("Milestone amount more than fixed amount");
				document.getElementById("milestoneAmount"+cnt).value = "";
			}
		}
	}
	
	function getExchangeValue() {
		
		var strCurrency = '';
		var strBillingCurrency = '';
		if(document.getElementById("strCurrency")) {
			strCurrency = document.getElementById("strCurrency").value;
		}
		if(document.getElementById("strBillingCurrency")) {
			strBillingCurrency = document.getElementById("strBillingCurrency").value;
		}	
		 if(strBillingCurrency == strCurrency || strBillingCurrency == '' || strBillingCurrency == '0') {
			 document.getElementById("exchangevalueSpan").innerHTML = "";
		 } else {
		 	getContent('exchangevalueSpan', 'ViewAndUpdateExchangeValue.action?strBillingCurrId='+strBillingCurrency+'&strReportCurrId='+strCurrency+'&type=ExV');
		 }
		 
		 window.setTimeout( 
		    function() {
		    	calBillingAmount();
		    }, 700);
	}
	
	
	function calBillingAmount() {
		var strCurrency = '';
		var strBillingCurrency = '';
		var billingAmt = '';
		if(document.getElementById("billingAmountF")) {
			billingAmt = document.getElementById("billingAmountF").value;
		}
		if(document.getElementById("strCurrency")) {
			strCurrency = document.getElementById("strCurrency").value;
		}
		if(document.getElementById("strBillingCurrency")) {
			var strBillingCurrency = document.getElementById("strBillingCurrency").value;
		}
		if(strBillingCurrency == strCurrency || strBillingCurrency == '' || strBillingCurrency == '0') {
			document.getElementById("exchangeAmountSpan").innerHTML = '';
		} else {
			var exchangeValueHide = document.getElementById("exchangeValueHide").value;
			var otherCurrVal = 0;
			if(billingAmt == '') { 
				billingAmt = 0;
			}
			if(parseFloat(exchangeValueHide) > 0) {
				otherCurrVal = parseFloat(billingAmt) * parseFloat(exchangeValueHide);
			}
			document.getElementById("exchangeAmountSpan").innerHTML = 'Billing Amount: ' + otherCurrVal.toFixed(1);
		}
	}

	
	var optBHDatatype = '<%=request.getAttribute("sbBHDatatype")%>';
	var optBHOtherVariable = '<%=request.getAttribute("sbBHOtherVariable")%>';
	
	function addNewBillingHead() { 
		var headCnt = document.getElementById("billingheadcount").value;
			var cnt=(parseInt(headCnt)+1);

			var table = document.getElementById("billingHeadTable");
		    var rowCount = table.rows.length;
		    var row = table.insertRow(rowCount);
		    
		    row.id="billingHeadTR"+cnt;
		    var cell0 = row.insertCell(0);
		    cell0.setAttribute('valign', 'top');
		    cell0.innerHTML = "<input type=\"hidden\" name=\"billingHeadTRId\" id=\"billingHeadTRId"+cnt+"\" value=\""+cnt+"\">"+
		    "<input type=\"hidden\" name=\"mBillingHeadId\" id=\"mBillingHeadId"+cnt+"\" value=\"0\" />"+
		    "<input type=\"text\" name=\"billingHeadLabel\" id=\"billingHeadLabel"+cnt+"\" class=\"validateRequired\" style=\"float: left; width:260px !important;\">"
		    +"<a href=\"javascript:void(0)\" onclick=\"addNewBillingHead();\" title=\"Add new billing head\" style=\"float: left;\"  class=\"fa fa-fw fa-plus\">&nbsp;</a>"+
			"<a href=\"javascript:void(0)\" onclick=\"deleteBillingHead('"+cnt+"', '0')\" id=\""+cnt+"\" style=\"float: left;\" class=\"fa fa-fw fa-remove\">&nbsp;</a>";
		    
			var cell1 = row.insertCell(1);
			 cell1.setAttribute('valign', 'top');
			 cell1.innerHTML = "<span id=\"bhDataTypeSpan"+cnt+"\">"+ optBHDatatype +"</span>";
			
			var cell2 = row.insertCell(2);
			cell2.setAttribute('valign', 'top');
			cell2.innerHTML = "<span id=\"bhOtherVariableSpan"+cnt+"\" style=\"display: none;\">"+optBHOtherVariable +"</span>";
			
		    document.getElementById("billingheadcount").value = cnt;
		    
		    document.getElementById("billingHeadDataType").id = "billingHeadDataType"+cnt;
		    
		    document.getElementById("billingHeadOtherVariable").name = "billingHeadOtherVariable"+cnt;
		    document.getElementById("billingHeadOtherVariable").id = "billingHeadOtherVariable"+cnt;
		    
		    document.getElementById("billingHeadDataType"+cnt).setAttribute("onchange", "showOtherVariables(this.value, '"+cnt+"');");
		}
		
	function deleteBillingHead(count, billingHeadId) {
		if(confirm('Are you sure, you want to delete this billing head?')) {
			if(parseFloat(billingHeadId) > 0) {
				getContent('deleteSpan', 'DeleteBillingHead.action?billingHeadId='+billingHeadId);
			}
			var trIndex = document.getElementById("billingHeadTR"+count).rowIndex;
		    document.getElementById("billingHeadTable").deleteRow(trIndex);
		}
	}
	
	
	function showOtherVariables(value, count) {
		if(value == <%=IConstants.DT_PRORATA_INDIVIDUAL %> || value == <%=IConstants.DT_OPE_INDIVIDUAL %>) {
			document.getElementById("bhOtherVariableSpan"+count).style.display = 'block';
			getContent('bhOtherVariableSpan'+count, 'GetBillingHeadOtherVariable.action?billingHeadDataType='+value+'&count='+count);
		} else {
			document.getElementById("bhOtherVariableSpan"+count).style.display = 'none';
		}
	}
	
	
	function submitAndProcced(formId, step) {
		if(document.getElementById("submitBtnTable_"+step)) {
			document.getElementById("submitBtnTable_"+step).style.display = "none";
		}
		var form_data = $("#"+formId).serialize();
		//alert("form_data ===>> " + form_data);
		$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PreAddNewProject1.action?submit=SubmitAndProceed',
			data: form_data,
			success: function(result) {
				$("#subSubDivResult").html(result);
				document.getElementById("proDetail").className = "";
				document.getElementById("proSnapshot").className = "";
				document.getElementById("proStep1").className = "";
				document.getElementById("proStep2").className = "";
				document.getElementById("proStep3").className = "";
				document.getElementById("proStep4").className = "";
				/* document.getElementById("proStep5").className = ""; */
				document.getElementById("proStep6").className = "";
				document.getElementById("proStep7").className = "";
				document.getElementById("proStep8").className = "";
				var intStep = (parseInt(step)+1);
				 if(step == 4) {
				 	intStep = (parseInt(step)+2);
				 }
				document.getElementById("proStep"+intStep).className = "active";
	   		}
		});
	}
	
	
	function saveAndExit(formId, step) {
		if(document.getElementById("submitBtnTable_"+step)) {
			document.getElementById("submitBtnTable_"+step).style.display = "none";
		}
		var proType = document.getElementById("proType").value;
		var form_data = $("#"+formId).serialize();
		//alert("form_data ===>> " + form_data);
		$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PreAddNewProject1.action?submit=SaveAndExit',
			data: form_data,
			success: function(result) {
				/* $("#subSubDivResult").html(result); */
				getAllProjectNameList('AllProjectNameList', proType);
			}
		});
	}
	
	
</script>

	<% 
		List<List<String>> proMilestoneList = (List<List<String>>)request.getAttribute("proMilestoneList"); 
	%>
	<s:form id="frmAddProject_6" cssClass="formcss"
		action="PreAddNewProject1" name="frmAddProject_6" method="post"
		enctype="multipart/form-data" theme="simple">
		<div class="col-lg-12 col-md-12 col-sm-12">
			<s:hidden name="step" value="6"></s:hidden>
			<s:hidden name="operation"></s:hidden>
			<s:hidden name="pageType"></s:hidden>
			<s:hidden name="proType" id="proType"></s:hidden>
			<s:hidden name="pro_id"></s:hidden>

			<table border="0" class="table table_no_border">
				<tr>
					<td>
						<table class="table table_no_border">
							<tr>
								<td colspan=2>
									<div style="border-bottom: 1px solid #346897; font-weight: bold; margin-left: 90px;">Estimation Summary:</div></td>
							</tr>
							<tr>
								<td nowrap="nowrap" class="txtlabel alignRight">Reporting Currency:</td>
								<td><%=uF.showData((String)request.getAttribute("strCurrency"), "-") %>
								</td>
							</tr>

							<tr>
								<td nowrap="nowrap" class="txtlabel alignRight">Billing Currency:</td>
								<td>
									<div style="float: left;"><%=uF.showData((String)request.getAttribute("strBillingCurrency"), "-") %>
									</div>
									<div style="float: left; margin-left: 7px; margin-top: 2px;" id="exchangevalueSpan"></div>
								</td>
							</tr>

							<tr>
								<td nowrap="nowrap" class="txtlabel alignRight">Project Billing Type:</td>
								<td><s:hidden name="billingType" id="idBilling" /> <%=uF.showData((String)request.getAttribute("billingTypeName"), "-") %>
								</td>
							</tr>

							<tr id="DailyTR" style="display: none;">
								<td nowrap="nowrap" class="txtlabel alignRight">&nbsp;</td>
								<td>
									<% 
								String hoursForDay = (String) request.getAttribute("hoursForDay");
								String hoursToDay = (String) request.getAttribute("hoursToDay");
								String hoursToDay1 = "";
								String hoursToDay2 = "";
								String hoursForDayR = "readonly='readonly'";
								String hoursForDayValid = "";
								if(hoursToDay != null && hoursToDay.equals("1")) {
									hoursToDay1 = "checked='checked'";
								} else if(hoursToDay != null && hoursToDay.equals("2")) {
									hoursToDay2 = "checked='checked'";
									hoursForDayR = "";
									hoursForDayValid = "class='validateRequired'";
								}
								%> <span style="width: 100%; float: left;"> <input type="radio" name="hoursToDay" id="hoursToDay" value="1" disabled="disabled" <%=hoursToDay1 %>
										onclick="enableTextfield(this.value)">If Daily, one single hr entry is considered 1 day<br> <i> If multiple tasks are taken up in one day, the task hrs are
											equally distributed</i> </span> <span style="width: 100%; float: left; margin-top: 10px;"> 
										<input type="radio" name="hoursToDay" id="hoursToDay" value="2" disabled="disabled" <%=hoursToDay2 %> onclick="enableTextfield(this.value)">
											If Daily, convert no. of hrs to Days. One Day = <input type="text" name="hoursForDay" id="hoursForDay" style="width: 30px !important;" onkeyup="ckeckHourRange(this.value)" readonly="readonly" 
											onkeypress="return isNumberKey(event)" <%=hoursForDayValid %> value="<%=uF.showData(hoursForDay, "") %>" <%=hoursForDayR %> /> hrs </span>
								</td>
							</tr>

							<tr id="idFBillingAmount"
								<%
								String billingType=(String)request.getAttribute("billingType");
								if(billingType==null || billingType.equals("H") || billingType.equals("D") || billingType.equals("M")) { %>
								style="display: none;" <% } %>>
								<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Entered Fixed Amount:</td>
								<td>
									<div style="float: left;"><%=uF.showData((String)request.getAttribute("strCurrency"), "-") %>
										&nbsp;
										<%=uF.showData((String)request.getAttribute("billingAmountF"), "") %>
									</div>
									<div style="float: left; margin-left: 7px; margin-top: 2px;" id="exchangeAmountSpan"></div>
								</td>
							</tr>


							<tr id="idFActualBilling"
								<% if(billingType==null || billingType.equals("H") || billingType.equals("D") || billingType.equals("M")) { %>
								style="display: none;" <% } %>>
								<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Selected Basis of Assignment Costing:</td>
								<td><s:hidden name="strActualBilling" id="idBasisCosting" />
									<%=uF.showData((String)request.getAttribute("strActualBillingName"), "-") %>
								</td>
							</tr>

							<tr>
								<td nowrap="nowrap" class="txtlabel alignRight" valign="top"><span id="idIdealTime">Estimated man-hours or man-days</span>:</td>
								<td><%=uF.showData((String)request.getAttribute("estimatedHours"), "-") %></td>
							</tr>

							<tr>
								<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Selected Billing Frequency:</td>
								<td nowrap="nowrap"><%=uF.showData((String)request.getAttribute("strBillingKindName"), "-") %>
									<s:hidden name="strBillingKind" id="strBillingKind" /></td>
							</tr>
							<tr>
								<td nowrap="nowrap" class="txtlabel alignRight" valign="top" style="width: 95px;"><span id="billingCycle" style="display: none;">Billing Cycle:</span>
								</td>
								<td><span id="weekly" style="display: none; margin-left: 15px;">Day: <%=uF.showData((String)request.getAttribute("weekdayCycle"), "-") %>
								</span> <span id="monthly" style="display: none; margin-left: 15px;">Date of Month: <%=uF.showData((String)request.getAttribute("dayCycle"), "-") %>
								</span></td>
							</tr>
							<tr>
								<td colspan="2" class="alignRight">
									<a href="javascript:void(0);" onclick="getProjectInfo('PreAddNewProject1', '', '&step=0&operation=E');">change estimation summary</a>
								</td>
							</tr>
						</table></td>
					<!-- </tr>
					
					<tr> -->
					<td valign="top">
						<table class="table table_no_border" style="margin-left: 7px;">
							<tr>
								<td colspan=2>
									<div style="border-bottom: 1px solid #346897; font-weight: bold; margin-left: 90px;">Bill Reference Heads:</div></td>
							</tr>
							<tr>
								<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Account Reference:</td>
								<td><s:textfield name="strAccountRef" id="strAccountRef"></s:textfield>
								</td>
							</tr>
							<tr>
								<td nowrap="nowrap" class="txtlabel alignRight" valign="top">P.O. No.:</td>
								<td><s:textfield name="strPONo" id="strPONo"></s:textfield>
								</td>
							</tr>
							<tr>
								<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Terms:</td>
								<td><s:textarea name="strTerms" id="strTerms" rows="3" cols="25" />
								</td>
							</tr>
							<tr>
								<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Due Date:</td>
								<td><s:textfield name="strDueDate" id="strDueDate"></s:textfield>
								</td>
							</tr>
						</table></td>
				</tr>


				<tr id="dependencyTR" style="display: none;">
					<td nowrap="nowrap" class="txtlabel alignRight" valign="top" style="padding-right: 35px;">Dependent on:<sup>*</sup>&nbsp;&nbsp;&nbsp;
						<s:select name="milestoneDependentOn" id="milestoneDependentOn" cssClass="validateRequired" headerKey=""
							headerValue="Select Dependency" list="#{'1':'% of Completion', '2':'End of a Task'}" onchange="showMilestoneData(this.value);" />
					</td>
					<td>&nbsp;</td>
				</tr>

				<tr>
					<td colspan="2">
						<div id="milestoneDiv" style="display: none;">
							<input type="hidden" name="hideMilestones" id="hideMilestones" value="<%=(proMilestoneList != null && !proMilestoneList.isEmpty()) ? proMilestoneList.size() : "1" %>" />
							<input type="hidden" name="hideExsitMilestones" id="hideExsitMilestones"
								value="<%=(proMilestoneList != null && !proMilestoneList.isEmpty()) ? proMilestoneList.size()-1 : "0" %>" />

							<div id="milestoneHeadDiv">
								<table id="milestoneHeadTable" class="table table_no_border"> <!-- table-bordered -->
									<!-- <tr>
											<td colspan="5"><hr style="height: 1px; width: 100%; background-color: #346897;">
											</td>
										</tr> -->
									<tr>
										<td valign="top" style="color: #777777; font-family: verdana, arial, helvetica, sans-serif; padding-left: 7px; font-size: 12px; font-style: normal; background-color: lightgray;">Milestone Name</td>
										<td valign="top" style="color: #777777; font-family: verdana, arial, helvetica, sans-serif; padding-left: 7px; font-size: 12px; font-style: normal; background-color: lightgray;">Milestone Description</td>
										<td valign="top" style="color: #777777; font-family: verdana, arial, helvetica, sans-serif; padding-left: 7px; font-size: 12px; font-style: normal; background-color: lightgray;"><span id="milestoneTaskLabel"> Milestone %/ Task</span></td>
										<td valign="top" style="color: #777777; font-family: verdana, arial, helvetica, sans-serif; padding-left: 7px; font-size: 12px; font-style: normal; background-color: lightgray;">Milestone Amount</td>
										<td valign="top" style="color: #777777; font-family: verdana, arial, helvetica, sans-serif; padding-left: 7px; font-size: 12px; font-style: normal; background-color: lightgray;">Actions</td>
									</tr>
									<% for(int i=0; proMilestoneList != null && i<proMilestoneList.size(); i++) { 
										List<String> innerList = proMilestoneList.get(i);
									%>
									<tr id="milestoneTR_<%=i %>">
										<td class="txtlabel" valign="top"><input type="hidden" name="milestoneId" id="milestoneId" value="<%=innerList.get(0) %>" /> 
											<input type="text" name="milestoneName" id="milestoneName<%=i %>" class="validateRequired" value="<%=innerList.get(1) %>" />
										</td>
										<td class="txtlabel" valign="top">
											<textarea rows="2" cols="25" name="milestoneDescription" id="milestoneDescription<%=i %>"><%=innerList.get(2) %></textarea>
										</td>

										<td class="txtlabel" valign="top"><span id="taskSpan<%=i %>" style="display: none;"> 
											<select name="projectTask" id="projectTask<%=i %>" class="validateRequired" style="width: 150px !important;">
													<option value="">Select Task</option>
													<%=innerList.get(4) %>
											</select> </span> <span id="milestonePercentSpan<%=i %>" style="display: none;">
											<input type="text" name="milestonePercent" id="milestonePercent<%=i %>" class="validateRequired" value="<%=innerList.get(3) %>" onkeyup="calculateMilestoneAmount('P','<%=i %>')" onchange="calculateMilestoneAmount('P','<%=i %>')" style="width: 110px !important;" onkeypress="return isNumberKey(event)" /> </span>
										</td>

										<td class="txtlabel" valign="top"><input type="text" name="milestoneAmount" id="milestoneAmount<%=i %>" class="validateRequired" value="<%=innerList.get(5) %>" onkeyup="calculateMilestoneAmount('A','<%=i %>')" onchange="calculateMilestoneAmount('A','<%=i %>')" style="width: 110px !important;" onkeypress="return isNumberKey(event)" /></td>
										<td valign="top" style="width: 185px;">
											<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
											<a href="javascript:void(0);" onclick="addMilestone();" class="fa fa-fw fa-plus" title="Add">&nbsp;</a> <% if(i > 0) { %>
											<a href="javascript:void(0);" style="float: left;" onclick="deleteMilestone(this.id);" id="<%=i %>" class="fa fa-fw fa-remove" title="Remove">&nbsp;</a> <% } %> <% } %>
										</td>
									</tr>
									<% } %>

									<%if(proMilestoneList == null || proMilestoneList.size()==0) { %>
									<tr id="milestoneTR_0">
										<td class="txtlabel" valign="top"><input type="text" name="milestoneName" id="milestoneName0" class="validateRequired" />
										</td>
										<td class="txtlabel" valign="top"><textarea rows="2" cols="25" name="milestoneDescription" id="milestoneDescription0"></textarea>
										</td>
										<td class="txtlabel" valign="top"><span id="taskSpan0" style="display: none;"> 
											<select name="projectTask" id="projectTask0" class="validateRequired" style="width: 150px !important;">
												<option value="">Select Task</option>
												<%=(String)request.getAttribute("sbOption") %>
											</select> </span> <span id="milestonePercentSpan0" style="display: none;">
											<input type="text" name="milestonePercent" id="milestonePercent0" class="validateRequired" onkeyup="calculateMilestoneAmount('P','0');" onchange="calculateMilestoneAmount('P','0');" style="width: 110px !important;" onkeypress="return isNumberKey(event);" /> </span>
										</td>

										<td class="txtlabel" valign="top">
											<input type="text" name="milestoneAmount" id="milestoneAmount0" class="validateRequired" onkeyup="calculateMilestoneAmount('A','0');" 
											onchange="calculateMilestoneAmount('A','0');" style="width: 110px !important;" onkeypress="return isNumberKey(event)" />
										</td>
										<td valign="top" style="width: 185px;"><a href="javascript:void(0);" onclick="addMilestone();" class="fa fa-fw fa-plus" title="Add">&nbsp;</a></td>
									</tr>
									<% } %>

									<tr id="milestoneTotalTR">
										<td valign="top" style="padding-left: 7px; width: 222px;">&nbsp;</td>
										<td valign="top" align="right" style="color: #777777; font-family: verdana, arial, helvetica, sans-serif; padding-right: 10px; padding-top: 12px; font-size: 11px; font-style: normal; font-weight: 600; width: 227px;">Total:</td>
										<td valign="top" class="txtlabel"><span id="milestoneTotPercentSpan"> 
											<input type="text" name="milestoneTotPercent" id="milestoneTotPercent" value="100" style="width: 110px !important;" readonly="readonly" /> </span>
										</td>
										<td valign="top" class="txtlabel"><input type="text" name="milestoneTotAmount" id="milestoneTotAmount" value="<%=(String)request.getAttribute("billingAmountF") %>" style="width: 110px !important;" readonly="readonly" /></td>
									</tr>

								</table>
							</div>

							<%-- <% for(int i=0; proMilestoneList != null && i<proMilestoneList.size(); i++) { 
									List<String> innerList = proMilestoneList.get(i);
								%>
								<div id="milestonesubDiv<%=i %>"
									style="float: left; width: 100%;">
									<table>
										<tr>
											<td colspan="5"><input type="hidden" name="milestoneId" id="milestoneId" value="<%=innerList.get(0) %>" />
												<hr style="height: 1px; width: 100%; background-color: #346897;">
											</td>
										</tr>
										<tr>
											<td class="txtlabel" valign="top"><input type="text" name="milestoneName" id="milestoneName<%=i %>" class="validateRequired" value="<%=innerList.get(1) %>" /></td>

											<td class="txtlabel" valign="top"><textarea rows="2" cols="25" name="milestoneDescription" id="milestoneDescription<%=i %>"><%=innerList.get(2) %></textarea>
											</td>

											<td class="txtlabel" valign="top"><span id="taskSpan<%=i %>" style="display: none;"> 
												<select name="projectTask" id="projectTask<%=i %>" class="validateRequired">
														<option value="">Select Task</option>
														<%=innerList.get(4) %>
												</select> </span> <span id="milestonePercentSpan<%=i %>" style="display: none;"> 
												<input type="text" name="milestonePercent" id="milestonePercent<%=i %>" class="validateRequired" value="<%=innerList.get(3) %>"
													onkeyup="calculateMilestoneAmount('P','<%=i %>')" onchange="calculateMilestoneAmount('P','<%=i %>')"
													onkeypress="return isNumberKey(event)" /> </span></td>

											<td class="txtlabel" valign="top"><input type="text" name="milestoneAmount" id="milestoneAmount<%=i %>" class="validateRequired" value="<%=innerList.get(5) %>"
												onkeyup="calculateMilestoneAmount('A','<%=i %>')" onchange="calculateMilestoneAmount('A','<%=i %>')"
												onkeypress="return isNumberKey(event)" /></td>
											<td valign="top" style="width: 185px;"><a href="javascript:void(0);" onclick="addMilestone();" class="fa fa-fw fa-plus" title="Add">&nbsp;</a> 
											<% if(i > 0) { %>
												<a href="javascript:void(0);" style="float: left;" onclick="removeMilestone(this.id);" id="<%=i %>" class="fa fa-fw fa-remove" title="Remove">&nbsp;</a> 
											<% } %>
											</td>
										</tr>
									</table>
								</div>

								<% } %> --%>

							<%-- <%if(proMilestoneList == null || proMilestoneList.size()==0) { %>
								<div id="milestonesubDiv0" style="float: left; width: 100%;">
									<table>
										<tr>
											<td colspan="5"><hr style="height: 1px; width: 100%; background-color: #346897;"></td>
										</tr>
										<tr>
											<td class="txtlabel" valign="top"><input type="text" name="milestoneName" id="milestoneName0" class="validateRequired" /></td>
											<td class="txtlabel" valign="top"><textarea rows="2" cols="25" name="milestoneDescription" id="milestoneDescription0"></textarea></td>
											<td class="txtlabel" valign="top">
												<span id="taskSpan0" style="display: none;">
													<select name="projectTask" id="projectTask0" class="validateRequired">
														<option value="">Select Task</option>
														<%=(String)request.getAttribute("sbOption") %>
													</select>
												</span>
												<span id="milestonePercentSpan0" style="display: none;">
													<input type="text" name="milestonePercent" id="milestonePercent0" class="validateRequired" onkeyup="calculateMilestoneAmount('P','0');" onchange="calculateMilestoneAmount('P','0');" onkeypress="return isNumberKey(event);" />
												</span>
											</td>

											<td class="txtlabel" valign="top"><input type="text" name="milestoneAmount" id="milestoneAmount0" class="validateRequired" onkeyup="calculateMilestoneAmount('A','0');" onchange="calculateMilestoneAmount('A','0');" onkeypress="return isNumberKey(event)" /></td>
											<td valign="top" style="width: 185px;"><a href="javascript:void(0);" onclick="addMilestone();" class="fa fa-fw fa-plus" title="Add">&nbsp;</a>
											</td>
										</tr>
									</table>
								</div>
								<% } %> --%>

						</div> <%-- <div id="milestoneTotAmtDiv" style="display: none; float: left; width: 100%; height: 30px;">
								<table>
									<tr>
										<td colspan="5"><hr style="height: 1px; width: 100%; background-color: #346897;">
										</td>
									</tr>
									<tr>
										<td valign="top" style="padding-left: 7px; width: 222px;">&nbsp;</td>

										<td valign="top" align="right" style="color: #777777; font-family: verdana, arial, helvetica, sans-serif; padding-right: 10px; padding-top: 12px; font-size: 11px; font-style: normal; font-weight: 600; width: 227px;">Total:</td>

										<td valign="top" class="txtlabel"><span id="milestoneTotPercentSpan"> <input type="text" name="milestoneTotPercent" id="milestoneTotPercent" value="100" style="width: 100px !important;" readonly="readonly" /> </span>
										</td>

										<td valign="top" class="txtlabel"><input type="text" name="milestoneTotAmount" id="milestoneTotAmount" value="<%=(String)request.getAttribute("billingAmountF") %>" style="width: 180px !important;" readonly="readonly" />
										</td>
										<td valign="top" style="width: 175px;">&nbsp;</td>
									</tr>
								</table>
							</div> --%></td>
				</tr>
			</table>

			<br />

			<!-- <table id="billingHeadTable" class="table_style" style="margin-left: 60px;"> -->
			<table id="billingHeadTable" border="0" class="table table_no_border">
				<tr>
					<td colspan="3" style="border-bottom: 1px solid #346897; font-weight: bold;">Billing Heads:</td>
				</tr>

				<tr id="billingHeadTR0">
					<td nowrap="nowrap" class="txtlabel" style="width: 50%;"><span id="deleteSpan"></span> <input type="hidden" name="billingheadcount" id="billingheadcount" value="2" /> <strong>Billing Head (Label)</strong></td>
					<td nowrap="nowrap" class="txtlabel"><strong>Data Type</strong></td>
					<td nowrap="nowrap" class="txtlabel"><strong>Other Variables</strong></td>
				</tr>

				<% 	Map<String, List<String>> hmProBillingHeadData = (Map<String, List<String>>) request.getAttribute("hmProBillingHeadData");
						Map<String, List<String>> hmBillingHeadData = (Map<String, List<String>>) request.getAttribute("hmBillingHeadData");
					
					if(hmProBillingHeadData != null && !hmProBillingHeadData.isEmpty()) {
					Iterator<String> it = hmProBillingHeadData.keySet().iterator();
						int i = 0;
						while(it.hasNext()) {
						String billingHeadId = it.next();
						List<String> innerList = hmProBillingHeadData.get(billingHeadId);
						i++;
					%>

				<tr id="billingHeadTR<%=i%>">
					<td nowrap="nowrap" valign="top"><input type="hidden" name="billingHeadTRId" id="billingHeadTRId<%=i%>" value="<%=i %>" />
						<input type="hidden" name="billingHeadId" id="billingHeadId<%=i %>" value="<%=innerList.get(0) %>" /> 
						<input type="hidden" name="mBillingHeadId" id="mBillingHeadId<%=i %>" value="<%=innerList.get(5) %>" />
						<input type="text" name="billingHeadLabel" id="billingHeadLabel1" style="float: left; width: 260px !important;" value="<%=innerList.get(1) %>" /> <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
						<a href="javascript:void(0)" onclick="addNewBillingHead();" title="Add new billing head" style="float: left;" class="fa fa-fw fa-plus">&nbsp;</a> <% if(i > 1) { %> 
						<a href="javascript:void(0)" onclick="deleteBillingHead('<%=i %>', '<%=innerList.get(0) %>')" id="" style="float: left;" class="fa fa-fw fa-remove" title="Remove this billing head">&nbsp;</a> <% } %> <% } %>
					</td>
					<td valign="top"><span id="bhDataTypeSpan<%=i%>"> <select name="billingHeadDataType" id="billingHeadDataType<%=i %>" style="width: 150px !important;" onchange="showOtherVariables(this.value, '<%=i %>');">
							<option value="">Select Data Type</option>
							<%=innerList.get(2) %>
						</select> </span></td>
					<% 
					String ovDisplay = "none";
					if(uF.parseToInt(innerList.get(4)) == IConstants.DT_PRORATA_INDIVIDUAL || uF.parseToInt(innerList.get(4)) == IConstants.DT_OPE_INDIVIDUAL) { 
						ovDisplay = "block";
					}
					%>
					<td valign="top"><span id="bhOtherVariableSpan<%=i%>" style="display: <%=ovDisplay %>;"> <select name="billingHeadOtherVariable<%=i%>"
							id="billingHeadOtherVariable<%=i%>" style="width: 160px !important;">
							<option value="">Select Other Variable</option>
							<%=innerList.get(3) %>
						</select> </span></td>
				</tr>

				<% } %>

				<% } else if(hmBillingHeadData != null && !hmBillingHeadData.isEmpty()) { 
				
							Iterator<String> it = hmBillingHeadData.keySet().iterator();
							int i = 0;
							while(it.hasNext()) {
							String billingHeadId = it.next();	
							List<String> innerList = hmBillingHeadData.get(billingHeadId);
							i++;
					%>

				<tr id="billingHeadTR<%=i%>">
					<td nowrap="nowrap" valign="top"><input type="hidden" name="billingHeadTRId" id="billingHeadTRId<%=i%>" value="<%=i %>" />
						<input type="hidden" name="mBillingHeadId" id="mBillingHeadId<%=i %>" value="<%=innerList.get(0) %>" /> 
						<input type="text" name="billingHeadLabel" id="billingHeadLabel1" style="float: left; width: 260px !important;"
						value="<%=innerList.get(1) %>" /> <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
						<a href="javascript:void(0)" onclick="addNewBillingHead();" style="float: left;" title="Add new billing head" class="fa fa-fw fa-plus">&nbsp;</a> 
						<% if(i > 1) { %> <a href="javascript:void(0)" onclick="deleteBillingHead('<%=i %>', '0')" style="float: left;" class="fa fa-fw fa-remove" title="Remove this billing head">&nbsp;</a>
						<% } %> <% } %>
					</td>
					<td valign="top"><span id="bhDataTypeSpan<%=i%>"> <select name="billingHeadDataType" id="billingHeadDataType<%=i %>"
							style="width: 150px !important;" onchange="showOtherVariables(this.value, '<%=i %>');">
								<option value="">Select Data Type</option>
								<%=innerList.get(2) %>
						</select> </span></td>
					<% 
							String ovDisplay = "none";
							if(uF.parseToInt(innerList.get(4)) == IConstants.DT_PRORATA_INDIVIDUAL || uF.parseToInt(innerList.get(4)) == IConstants.DT_OPE_INDIVIDUAL) { 
								ovDisplay = "block";
							}
						%>
					<td valign="top"><span id="bhOtherVariableSpan<%=i%>" style="display: <%=ovDisplay %>;"> <select name="billingHeadOtherVariable<%=i%>"
							id="billingHeadOtherVariable<%=i%>" style="width: 160px !important;">
								<option value="">Select Other Variable</option>
								<%=innerList.get(3) %>
						</select> </span></td>
				</tr>

				<% } %>

				<% } else { %>
				<tr id="billingHeadTR1">
					<td nowrap="nowrap" valign="top"><input type="hidden"
						name="billingHeadTRId" id="billingHeadTRId1" value="1" /> <input
						type="hidden" name="mBillingHeadId" id="mBillingHeadId1" value="0" />
						<s:textfield name="billingHeadLabel" id="billingHeadLabel1"
							cssStyle="float: left; width: 260px !important;"
							value="Service Charges" /> <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
						<a href="javascript:void(0)" onclick="addNewBillingHead();" style="float: left;" title="Add new billing head" class="fa fa-fw fa-plus">&nbsp;</a> <!-- <a href="javascript:void(0)" onclick="deleteBillingHead('1')" id="" style="float: left;" class="remove" title="Remove this billing head" >Remove</a> -->
						<% } %>
					</td>
					<td valign="top"><span id="bhDataTypeSpan1"> <s:select id="billingHeadDataType1" name="billingHeadDataType"
							cssStyle="width: 150px !important;" listKey="headId" listValue="headName" headerKey="" headerValue="Select Data Type"
							list="billingHeadDataTypeList" onchange="showOtherVariables(this.value, '1');" /> </span></td>

					<td valign="top"><span id="bhOtherVariableSpan1" style="display: none;"> <s:select id="billingHeadOtherVariable1" name="billingHeadOtherVariable1"
							cssStyle="width: 160px !important;" listKey="headId" listValue="headName" headerKey="" headerValue="Select Other Variable"
							list="billingHeadOtherVariableList" /> </span></td>
				</tr>
				<tr id="billingHeadTR2">
					<td nowrap="nowrap" valign="top"><input type="hidden" name="billingHeadTRId" id="billingHeadTRId2" value="2" /> <input type="hidden" name="mBillingHeadId" id="mBillingHeadId2" value="0" />
						<s:textfield name="billingHeadLabel" id="billingHeadLabel2" cssStyle="float: left; width: 260px !important;" value="Out Of Pocket Expenses" /> <% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
						<a href="javascript:void(0)" onclick="addNewBillingHead();" style="float: left;" title="Add new billing head" class="fa fa-fw fa-plus">&nbsp;</a> <!-- <a href="javascript:void(0)" onclick="deleteBillingHead('1')" id="" style="float: left;" class="remove" title="Remove this billing head" >Remove</a> -->
						<% } %>
					</td>
					<td valign="top"><span id="bhDataTypeSpan2"> <s:select id="billingHeadDataType2" name="billingHeadDataType"
								cssStyle="width: 150px !important;" listKey="headId" listValue="headName" headerKey="" headerValue="Select Data Type"
								list="billingHeadDataTypeList" onchange="showOtherVariables(this.value, '2');" /> </span></td>

					<td valign="top"><span id="bhOtherVariableSpan2" style="display: none;"> <s:select id="billingHeadOtherVariable2" name="billingHeadOtherVariable2"
								cssStyle="width: 160px !important;" listKey="headId" listValue="headName" headerKey="" headerValue="Select Data Type" list="billingHeadOtherVariableList" /> </span></td>
				</tr>
				<% } %>
			</table>


			<br />
			<table id="taxHeadTable" border="0" class="table table_no_border">
				<!-- <table id="taxHeadTable" class="table_style" style="margin-left: 60px;"> -->
				<tr>
					<td colspan="4" style="border-bottom: 1px solid #346897; font-weight: bold;">Tax Setting:</td>
				</tr>

				<tr id="taxHeadTR0">
					<td nowrap="nowrap" class="txtlabel" style="width: 35%;"><span id="taxHeadDeleteSpan"></span> 
						<input type="hidden" name="taxheadcount" id="taxheadcount" value="2" /> <strong>Tax Head (Label)</strong></td>
					<td nowrap="nowrap" class="txtlabel"><strong>Tax Percentage</strong>
					</td>
					<td nowrap="nowrap" class="txtlabel"><strong>Deduction Type</strong>
					</td>
					<td nowrap="nowrap" class="txtlabel"><strong>Status</strong>
					</td>
				</tr>

				<% 	Map<String, List<String>> hmProTaxHeadData = (Map<String, List<String>>) request.getAttribute("hmProTaxHeadData");
						Map<String, List<String>> hmTaxHeadData = (Map<String, List<String>>) request.getAttribute("hmTaxHeadData");
					
						if(hmProTaxHeadData != null && !hmProTaxHeadData.isEmpty()) {
						Iterator<String> it = hmProTaxHeadData.keySet().iterator();
						int i = 0;
						while(it.hasNext()) {
						String taxHeadId = it.next();	
						List<String> innerList = hmProTaxHeadData.get(taxHeadId);
						i++;
				%>

				<tr id="billingHeadTR<%=i%>">
					<td nowrap="nowrap" class="txtlabel" valign="top"><input type="hidden" name="taxHeadTRId" id="taxHeadTRId<%=i%>" value="<%=i %>" /> 
						<% if(innerList.get(0) != null && !innerList.get(0).equals("")) { %>
						<input type="hidden" name="taxHeadId" id="taxHeadId<%=i %>" value="<%=innerList.get(0) %>" /> <% } %> 
						<input type="hidden" name="mTaxHeadId" id="mTaxHeadId<%=i %>" value="<%=innerList.get(5) %>" /> 
						<input type="hidden" name="taxNameLabel" id="taxNameLabel<%=i %>" value="<%=innerList.get(6) %>" /> 
						<input type="text" name="taxHeadLabel" id="taxHeadLabel<%=i%>" style="float: left; width: 260px !important;" value="<%=innerList.get(1) %>" />
					</td>
					<td valign="top"><span id="thPercentSpan<%=i%>"> <input type="text" name="taxHeadPercent" id="taxHeadPercent<%=i%>"
							style="float: left; width: 120px !important;" value="<%=innerList.get(2) %>" onkeypress="return isNumberKey(event)" /> </span>
					</td>
					<td valign="top"><span id="thDeductionTypeSpan<%=i%>">
							<select name="taxHeadDeductionType" id="taxHeadDeductionType<%=i%>" style="width: 160px !important;">
								<option value="">Select Deduction Type</option>
								<%=innerList.get(3) %>
						</select> </span>
					</td>
					<td valign="top"><span id="thStatusSpan<%=i%>"> <select name="taxHeadStatus" id="taxHeadStatus<%=i%>" style="width: 120px !important;">
								<%=innerList.get(4) %>
						</select> </span>
					</td>
				</tr>

				<% } %>

				<% } else if(hmTaxHeadData != null && !hmTaxHeadData.isEmpty()) {

							Iterator<String> it = hmTaxHeadData.keySet().iterator();
								int i = 0;
								while(it.hasNext()) {
								String taxHeadId = it.next();	
								List<String> innerList = hmTaxHeadData.get(taxHeadId);
								i++;
					%>

				<tr id="billingHeadTR<%=i%>">
					<td nowrap="nowrap" class="txtlabel" valign="top">
						<input type="hidden" name="taxHeadTRId" id="taxHeadTRId<%=i%>" value="<%=i %>" /> 
						<input type="hidden" name="mTaxHeadId" id="mTaxHeadId<%=i %>" value="<%=innerList.get(0) %>" /> 
						<input type="hidden" name="taxNameLabel" id="taxNameLabel<%=i %>" value="<%=innerList.get(5) %>" /> 
						<input type="text" name="taxHeadLabel" id="taxHeadLabel<%=i%>" style="float: left; width: 260px !important;" value="<%=innerList.get(1) %>" />
					</td>
					<td valign="top"><span id="thPercentSpan<%=i%>"> 
						<input type="text" name="taxHeadPercent" id="taxHeadPercent<%=i%>" style="float: left; width: 120px !important;" value="<%=innerList.get(2) %>" onkeypress="return isNumberKey(event)" /> </span>
					</td>
					<td valign="top"><span id="thDeductionTypeSpan<%=i%>">
						<select name="taxHeadDeductionType" id="taxHeadDeductionType<%=i%>" style="width: 160px !important;">
							<option value="">Select Deduction Type</option>
							<%=innerList.get(3) %>
						</select> </span>
					</td>
					<td valign="top"><span id="thStatusSpan<%=i%>"> 
						<select name="taxHeadStatus" id="taxHeadStatus<%=i%>" style="width: 120px !important;">
								<%=innerList.get(4) %>
						</select> </span>
					</td>
				</tr>

				<% } %>

				<% } else { %>
				<tr>
					<td colspan="3">Taxs are not added.</td>
				</tr>
				<% } %>
			</table>

			<br />

			<table border="0" class="table table_no_border">
				<!-- <table class="table_style" style="margin-left: 60px;"> -->
				<tr>
					<td colspan=2 style="border-bottom: 1px solid #346897; font-weight: bold;">Payment Methods:</td>
				</tr>
				<tr>
					<td nowrap="nowrap" class="txtlabel" valign="top"><input type="checkbox" name="chkBank" <%=(String)request.getAttribute("chkBank") %>>Choose Bank: 
						<s:select name="bankName" listKey="bankId" listValue="bankName" headerKey="" headerValue="Select Bank" list="bankList" /> <br />
						<br /> <input type="checkbox" name="chkPaypal" <%=(String)request.getAttribute("chkPaypal") %>>Pay Pal: 
						<s:textfield name="strPaypal" id="strPaypal" />
					</td>
				</tr>
			</table>

			<br />
			<table border="0" class="table table_no_border">
				<tr>
					<td style="border-bottom: 1px solid #346897; font-weight: bold;">Additional Information:</td>
				</tr>
				<tr>
					<td><s:textarea name="invoiceAdditionalInfo" id="invoiceAdditionalInfo" rows="2" cols="150" /></td>
				</tr>
			</table>


			<br />
			<table border="0" class="table table_no_border">
				<tr>
					<td colspan=2 style="border-bottom: 1px solid #346897; font-weight: bold;">Invoice Template:</td>
				</tr>
				<tr>
					<td nowrap="nowrap" class="txtlabel alignRight" valign="top">Invoice Template:<sup>*</sup>
					</td>
					<td><s:select theme="simple" name="strInvoiceTemplate" id="strInvoiceTemplate" listKey="invoiceFormatId" listValue="invoiceFormatName" headerKey="0" headerValue="Default" list="invoiceTemplateList" key="" /></td>
				</tr>
			</table>


			<br />

		</div>
		<div class="clr"></div>

		<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
		<div style="margin: 0px 0px 0px 120px">
			<table id="submitBtnTable_6" border="0" class="table table_no_border">
				<tr>
					<td>
						<% if(operation != null && operation.equals("E")) { %> 
							<input type="button" name="skipProcced" value="Skip & Proceed" class="btn btn-primary" style="float: right; margin-right: 5px;" onclick="skipAndProcced('<%=request.getAttribute("pro_id") %>', '6', '<%=pageType %>');" />
						<% } %>
						<input type="submit" name="submit" value="Submit & Proceed" class="btn btn-primary" style="float: right; margin-right: 5px;" onclick="submitAndProcced('frmAddProject_6', '6');" /> 
						<input type="submit" name="stepSave" value="Save & Exit" class="btn btn-primary" style="float: right; margin-right: 5px;" onclick="saveAndExit('frmAddProject_6', '6');" /> 
						<input type="button" value="Cancel" class="btn btn-danger" style="float: right; margin-right: 5px;" name="cancel" onclick="closeForm('<%=pageType %>');"></td>
					<td></td>
				</tr>
			</table>
		</div>
		<% } %>
	</s:form>
</s:if>


<s:if test="step==7">
	<script>
	jQuery(document).ready(function() {
		//jQuery("#formID_7").validationEngine();
	});
	
	function deleteRow(id) {
	    try {
	    var row = document.getElementById(id);
		row.parentElement.removeChild(row); 
	     }catch(e) {
	        alert(e);
	    }
	}
	
	var cnt =0;
	
	function addVariableAmt() {
		cnt++;
		var divTag = document.createElement("div");
	    divTag.id = "row_task"+cnt;
		divTag.innerHTML = 	"<table class=\"table table_no_border\"><tr>"+
			"<td><input type=\"text\" name=\"variableName\"></td>"+
			"<td><input type=\"text\" name=\"variableAmount\" onkeypress=\"return isNumberKey(event)\" style=\"width:100px !important;\"></td>"+
			"<td><a href=\"javascript:void(0)\" onclick=\"addVariableAmt()\" class=\"fa fa-fw fa-plus\">&nbsp;</a>"+
			"<a href=\"javascript:void(0)\" onclick=\"removeVariableAmt(this.id)\" id=\""+cnt+"\" class=\"fa fa-fw fa-remove\">&nbsp;</a>"+
			"</td>"+
			"</tr></table>"; 
	    document.getElementById("div_tasks").appendChild(divTag);
	}
	
	
	function removeVariableAmt(removeId) {
		var remove_elem = "row_task"+removeId;
		var row_skill = document.getElementById(remove_elem); 
		document.getElementById("div_tasks").removeChild(row_skill);
	}

	
	function calculate(val,id,hours,evnt){
		document.getElementById("billableamount"+id).value=parseFloat(val)*parseFloat(hours);
	}
	
	
	function submitAndProcced(formId, step) {
		if(document.getElementById("submitBtnTable_"+step)) {
			document.getElementById("submitBtnTable_"+step).style.display = "none";
		}
		var form_data = $("#"+formId).serialize();
		$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PreAddNewProject1.action?submit=SubmitAndProceed',
			data: form_data,
			success: function(result) {
				$("#subSubDivResult").html(result);
				document.getElementById("proDetail").className = "";
				document.getElementById("proSnapshot").className = "";
				document.getElementById("proStep1").className = "";
				document.getElementById("proStep2").className = "";
				document.getElementById("proStep3").className = "";
				document.getElementById("proStep4").className = "";
				/* document.getElementById("proStep5").className = ""; */
				document.getElementById("proStep6").className = "";
				document.getElementById("proStep7").className = "";
				document.getElementById("proStep8").className = "";
				var intStep = (parseInt(step)+1);
				 if(step == 4) {
				 	intStep = (parseInt(step)+2);
				 }
				document.getElementById("proStep"+intStep).className = "active";
	   		}
		});
	}
	
	
	function saveAndExit(formId, step) {
		if(document.getElementById("submitBtnTable_"+step)) {
			document.getElementById("submitBtnTable_"+step).style.display = "none";
		}
		var proType = document.getElementById("proType").value;
		var form_data = $("#"+formId).serialize();
		$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PreAddNewProject1.action?submit=SaveAndExit',
			data: form_data,
			success: function(result) {
				/* $("#subSubDivResult").html(result); */
				getAllProjectNameList('AllProjectNameList', proType);
			}
		});
	}
	
	
</script>

	<s:form method="POST" name="frmAddProject_7" id="frmAddProject_7"
		action="PreAddNewProject1" theme="simple" cssStyle="float:left">
		<s:hidden name="ids"></s:hidden>
		<input type="hidden" name="actualBillingType"
			value="<%=(String)request.getAttribute("actualBillingType") %>" />
		<s:hidden name="step" value="7"></s:hidden>
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="pageType"></s:hidden>
		<s:hidden name="proType" id="proType"></s:hidden>
		<s:hidden name="pro_id"></s:hidden>

		<%
			String costLbl = "Hr";
			String estimateLbl = "hours";
			if((String)request.getAttribute("PROJECT_CALC_TYPE") != null && "D".equalsIgnoreCase((String)request.getAttribute("PROJECT_CALC_TYPE"))) {
				costLbl = "Day";
				estimateLbl = "days";
			} else if((String)request.getAttribute("PROJECT_CALC_TYPE") != null && "M".equalsIgnoreCase((String)request.getAttribute("PROJECT_CALC_TYPE"))) {
				costLbl = "Month";
				estimateLbl = "months";
			}
			%>
		<table border="0" class="table table_no_border">
			<tr>
				<th>Task Name</th>
				<th>Name</th>
				<th>skills</th>
				<th>Service</th>
				<th><%="Estimated <br/> man-"+estimateLbl %></th>

				<!-- Cost/Day & Budgeted Cost hide for KPCA  -->
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
				<th><%="Cost/"+costLbl %> *<br />(<%=uF.showData((String)request.getAttribute("SHORT_CURR"), "-") %>)</th>
				<th>Budgeted Cost<br />(<%=uF.showData((String)request.getAttribute("SHORT_CURR"), "-") %>)</th>
				<% } %>
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
				<th>Billable Rate **<br />(<%=uF.showData((String)request.getAttribute("SHORT_CURR"), "-") %>)</th>
				<th>Expected Billable Amount<br />(<%=uF.showData((String)request.getAttribute("SHORT_CURR"), "-") %>)</th>
				<% } %>
			</tr>
			<%	List<List<String>> alProjectCostReport = (List<List<String>>)request.getAttribute("alProjectCostReport");
		Map<String, List<List<String>>> hmProjectCostSubTaskReport = (Map<String, List<List<String>>>) request.getAttribute("hmProjectCostSubTaskReport");
		String service = (String)request.getAttribute("service"); 
		if(alProjectCostReport != null) {
			for(int i=0; i<alProjectCostReport.size(); i++) {
			List<String> alInner = alProjectCostReport.get(i);
			
		%>
			<tr>
				<td><input type="hidden" name="empID" id="empID"
					value="<%=alInner.get(10) %>" /> <%=alInner.get(1) %></td>
				<td><%=alInner.get(2) %></td>
				<td><%=alInner.get(3) %></td>
				<td><%=alInner.get(4) %></td>
				<td class="alignRight padRight20"><%=uF.showData(alInner.get(5), "0") %></td>

				<!-- Cost/Day & Budgeted Cost hide for KPCA  -->
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
				<td class="alignRight padRight20"><%=uF.showData(alInner.get(6), "0") %></td>
				<td class="alignRight padRight20"><%=uF.showData(alInner.get(7), "0") %></td>
				<% } %>

				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
				<td>
					<%if(request.getAttribute("FIXED")!=null) { %> 
						<input type="hidden" name="billablerate"><%=uF.showData(alInner.get(8), "0") %> 
					<% } else { %> 
					<input type="text" name="billablerate" readonly="readonly" onkeyup="calculate(this.value,'<%=i %>','<%=alInner.get(5) %>')" onkeypress="return isNumberKey(event)" style="width: 80px !important; text-align: right"
					<%if(alInner.get(8)!=null) { %> value="<%=uF.showData(alInner.get(8), "0") %>" <% } %>> <% } %>
				</td>
				<td>
					<%if(request.getAttribute("FIXED")!=null) { %> 
						<input type="hidden" name="billableamount"><%=uF.showData(alInner.get(9), "0") %> 
					<% } else { %> 
						<input type="text" name="billableamount" id="billableamount<%=i %>" readonly="readonly" style="width: 80px !important; text-align: right;"
					<%if(alInner.get(9)!=null) { %> value="<%=uF.showData(alInner.get(9), "0") %>" <% } %> /> <% } %>
				</td>
				<% } %>

			</tr>


			<%
			if(hmProjectCostSubTaskReport != null) {
			List<List<String>> alSubTasks = hmProjectCostSubTaskReport.get(alInner.get(0));
			for(int j=0; alSubTasks != null && j<alSubTasks.size(); j++) {
				List<String> innerList = alSubTasks.get(j);
			%>

			<tr>
				<td><%=innerList.get(1) %> [ST]</td>
				<td><%=innerList.get(2) %></td>
				<td><%=innerList.get(3) %></td>
				<td><%=innerList.get(4) %></td>
				<td class="alignRight padRight20">&nbsp;</td>

				<!-- Cost/Day & Budgeted Cost hide for KPCA  -->
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
				<td class="alignRight padRight20">&nbsp;</td>
				<td class="alignRight padRight20">&nbsp;</td>
				<% } %>
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<% } %>
			</tr>

			<%} }%>

			<% } } %>

		</table>


		<div id="div_tasks" style="margin-top: 50px; margin-bottom: 10px">
			<h3 style="margin-bottom: 20px">Other project specific expenses</h3>
			<%List<List<String>> variableList=(List<List<String>>)request.getAttribute("variableList");
				if(variableList == null) variableList = new ArrayList<List<String>>(); %>
			<table border="0" class="table table_no_border">
				<tr>
					<th>Description of Expense</th>
					<th>Estimated Amount (<%=uF.showData((String)request.getAttribute("SHORT_CURR"), "-") %>)</th>
					<th>&nbsp;</th>
				</tr>
				<% int i=0; for(; i<variableList.size(); i++) {
						List<String> alInner=variableList.get(i);
					%>
				<tr id="<%=i %>">
					<td><input type="text" value="<%=alInner.get(0) %>"
						name="variableName"></td>
					<td><input type="text" name="variableAmount"
						value="<%=alInner.get(1) %>" style="width: 100px !important;">
					</td>

					<td>
						<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
						<a href="javascript:void(0)" onclick="addVariableAmt()"
						class="fa fa-fw fa-plus" style="visibility: hidden;">&nbsp;</a> <% if(i>0) { %><a
						href="javascript:void(0)" onclick="removeVariableAmt('<%=i %>')"
						id="" class="fa fa-fw fa-remove">&nbsp;</a> <% } %> <% } %>
					</td>
				</tr>
				<% } if(variableList == null || variableList.size() == 0) { %>
				<tr id="<%=i %>">
					<td><input type="text" name="variableName">
					</td>
					<td><input type="text" name="variableAmount"
						onkeypress="return isNumberKey(event)"
						style="width: 100px !important;">
					</td>
					<td>
						<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
						<a href="javascript:void(0)" onclick="addVariableAmt()"
						class="fa fa-fw fa-plus">&nbsp;</a> <% } %>
					</td>
				</tr>
				<% } %>
			</table>
		</div>

		<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
		<div id="submitBtnTable_7" style="float: left; margin-left: 120px;">
			<% if(operation != null && operation.equals("E")) { %>
				<input type="button" name="skipProcced" value="Skip & Proceed" class="btn btn-primary" style="float: right; margin-right: 5px;" onclick="skipAndProcced('<%=request.getAttribute("pro_id") %>', '7', '<%=pageType %>');" />
			<% } %>
			<input type="submit" name="submit" value="Submit & Proceed" class="btn btn-primary" style="float: right; margin-right: 5px;" onclick="submitAndProcced('frmAddProject_7', '7');" /> 
			<input type="submit" name="stepSave" value="Save & Exit" class="btn btn-primary" style="float: right; margin-right: 5px;" onclick="saveAndExit('frmAddProject_7', '7');" /> 
			<input type="button" value="Cancel" class="btn btn-danger" style="float: right; margin-right: 5px;" name="cancel" onclick="closeForm('<%=pageType %>');">
		</div>
		<% } %>
	</s:form>

	<div
		style="font-size: 10px; line-height: 12px; margin-top: 20px; float: left">

		<!-- Cost/Day & Budgeted Cost hide for KPCA  -->
		<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
		<p>* Cost per hour is calculated as Total Gross Salary / 30 (Days
			in a month)/ Daily working man-hours</p>
		<p>* Cost per day is calculated as Total Gross Salary / 30 (Days
			in a month)</p>
		<% } %>
		<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
		<p>** Billable Rate is the rate charged by your company to the
			customers. It could be in man-days or man-hours depending upon the
			project.</p>
		<% } %>
	</div>

</s:if>


<s:if test="step==8">

	<s:form method="POST" name="frmAddProject_8" id="frmAddProject_8"
		action="PreAddNewProject1" theme="simple">
		<s:hidden name="ids"></s:hidden>
		<s:hidden name="step" value="8"></s:hidden>
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="pageType"></s:hidden>
		<s:hidden name="proType" id="proType"></s:hidden>
		<s:hidden name="pro_id"></s:hidden>
		<%
			String costLbl = "Hr";
			String estimateLbl = "hours";
			if((String)request.getAttribute("PROJECT_CALC_TYPE") != null && "D".equalsIgnoreCase((String)request.getAttribute("PROJECT_CALC_TYPE"))) {
				costLbl = "Day";
				estimateLbl = "days";
			} else if((String)request.getAttribute("PROJECT_CALC_TYPE") != null && "M".equalsIgnoreCase((String)request.getAttribute("PROJECT_CALC_TYPE"))) {
				costLbl = "Month";
				estimateLbl = "months";
			}
			%>

		<table border="0" class="table table_no_border">
			<tr>
				<th>Task Name</th>
				<th>Name</th>
				<th>skills</th>
				<th>Service</th>
				<th><%="Estimated <br/> man-"+estimateLbl %></th>

				<!-- Cost/Day & Budgeted Cost hide for KPCA  -->
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
				<th><%="Cost/"+costLbl %> *<br />(<%=uF.showData((String)request.getAttribute("SHORT_CURR"), "-") %>)</th>
				<th>Budgeted Cost<br />(<%=uF.showData((String)request.getAttribute("SHORT_CURR"), "-") %>)</th>
				<% } %>

				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
				<th>Billable Rate **<br />(<%=uF.showData((String)request.getAttribute("SHORT_CURR"), "-") %>)</th>
				<th>Expected Billable Amount<br />(<%=uF.showData((String)request.getAttribute("SHORT_CURR"), "-") %>)</th>
				<% } %>
			</tr>

			<%	List<List<String>> alReport = (List<List<String>>)request.getAttribute("alReport");
				Map<String, List<List<String>>> hmProjectSummarySubTaskReport = (Map<String, List<List<String>>>) request.getAttribute("hmProjectSummarySubTaskReport");
				String service=(String)request.getAttribute("service"); 
				double totalEstimatehours=0;
				double totalBudgetCost=0;
				double totalBillableCost=0;
					if(alReport!=null) { 
			
						for(int i=0;i<alReport.size();i++) {
						List<String> alInner=alReport.get(i);
						totalEstimatehours+=uF.parseToDouble(alInner.get(5));
						totalBudgetCost+=uF.parseToDouble(alInner.get(7));
						totalBillableCost+=uF.parseToDouble(alInner.get(9));
			%>
			<tr>
				<td><%=alInner.get(1) %></td>
				<td><%=alInner.get(2) %></td>
				<td><%=alInner.get(3) %></td>
				<td><%=alInner.get(4) %></td>
				<td class="alignRight padRight20"
					style="background-color: lightgreen"><%=uF.showData(alInner.get(5), "0") %></td>

				<!-- Cost/Day & Budgeted Cost hide for KPCA  -->
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
				<td class="alignRight padRight20"
					style="background-color: lightgreen"><%=uF.showData(alInner.get(6), "0") %></td>
				<td class="alignRight padRight20"
					style="background-color: lightgreen"><%=uF.showData(alInner.get(7), "0") %></td>
				<% } %>

				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
				<td class="alignRight padRight20" style="background-color: cyan"><%=uF.showData(alInner.get(8), "0") %></td>
				<td class="alignRight padRight20" style="background-color: cyan"><%=uF.showData(alInner.get(9), "0") %></td>
				<% } %>
			</tr>

			<% 
		if(hmProjectSummarySubTaskReport != null) {
			List<List<String>> subTaskList = hmProjectSummarySubTaskReport.get(alInner.get(0));
		
		for(int j=0; subTaskList!=null && j<subTaskList.size(); j++) {
			List<String> innerList = subTaskList.get(j);
		%>
			<tr>
				<td><%=innerList.get(1) %> [ST]</td>
				<td><%=innerList.get(2) %></td>
				<td><%=innerList.get(3) %></td>
				<td><%=innerList.get(4) %></td>
				<td class="alignRight padRight20" style="background-color: lightgreen">&nbsp;</td>

				<!-- Cost/Day & Budgeted Cost hide for KPCA  -->
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
				<td class="alignRight padRight20" style="background-color: lightgreen">&nbsp;</td>
				<td class="alignRight padRight20" style="background-color: lightgreen">&nbsp;</td>
				<% } %>

				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
				<td class="alignRight padRight20" style="background-color: cyan">&nbsp;</td>
				<td class="alignRight padRight20" style="background-color: cyan">&nbsp;</td>
				<% } %>
			</tr>
			<% } } %>

			<% } }
		if(request.getAttribute("FIXED")!=null) {
			totalBillableCost = uF.parseToDouble((String)request.getAttribute("FIXED"));
		}
	%>
			<tr style="border-top: 2px solid; border-top-color: #CCCCCC">
				<td><b>Total</b></td>
				<td></td>
				<td></td>
				<td></td>
				<td class="alignRight padRight20"
					style="background-color: lightgreen"><b><%=uF.formatIntoOneDecimal(totalEstimatehours) %></b>
				</td>

				<!-- Cost/Day & Budgeted Cost hide for KPCA  -->
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
				<td style="background-color: lightgreen">&nbsp;</td>
				<td class="alignRight padRight20"
					style="background-color: lightgreen"><b><%=uF.formatIntoOneDecimal(totalBudgetCost) %></b>
				</td>
				<% } %>
				<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
				<td style="background-color: cyan">&nbsp;</td>
				<td class="alignRight padRight20" style="background-color: cyan"><b><%=request.getAttribute("SHORT_CURR")%>
						<%= uF.formatIntoOneDecimal(totalBillableCost) %></b></td>
				<% } %>
			</tr>
		</table>

		<div style="margin-top: 50px; margin-bottom: 10px;">
			<h3 style="margin-bottom: 10px;">Project Projection</h3>
			<table border="0" class="table table_no_border">
				<tr>
					<th width="200px">Expected Gross Profit</th>
					<td width="200px" class="alignRight padRight20"><%=uF.showData((String)request.getAttribute("SHORT_CURR"), "-") %>
						<%=uF.formatIntoOneDecimal(totalBillableCost-totalBudgetCost) %></td>
				</tr>
				<tr>
					<th>Gross Profit Margin</th>
					<td class="alignRight padRight20"><%=(totalBudgetCost>0)? uF.formatIntoOneDecimal((totalBillableCost-totalBudgetCost)*100/totalBillableCost):"" %>%</td>
				</tr>
			</table>
		</div>
		<% if(proType == null || proType.equals("") || proType.equals("null") || proType.equalsIgnoreCase("L")) { %>
			<%-- <div><input type="button" name="stepSave" value="Finish" class="btn btn-primary" style="float: right; margin-right: 5px;" onclick="closeForm('<%=pageType %>');" /> </div> --%>
		<% } %>
	</s:form>


	<div
		style="float: left; width: 100%; font-size: 10px; line-height: 12px; margin-top: 20px;">
		<!-- Cost/Day & Budgeted Cost hide for KPCA  -->
		<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_COST"))) { %>
		<p>* Cost per hour is calculated as Total Gross Salary / 30 (Days
			in a month)/ Daily working man-hours</p>
		<p>* Cost per day is calculated as Total Gross Salary / 30 (Days
			in a month)</p>
		<% } %>

		<% if(uF.parseToBoolean(hmProInfoDisplay.get("IS_RATE"))) { %>
		<p>** Billable Rate is the rate charged by your company to the
			customers. It could be in man-days or man-hours depending upon the
			project.</p>
		<% } %>
	</div>

</s:if> 

<script>

if(document.getElementById("idBilling")) {
	checkBillingType('idBilling', 'OL');
}

if(document.getElementById("strBillingKind")) {
	var vall = document.getElementById("strBillingKind").value;
	hideShowMilestone(vall, 'OL');
}

if(document.getElementById("milestoneDependentOn")) {
	var val1 = document.getElementById("milestoneDependentOn").value;
	showMilestoneDataOnload(val1);
}

</script>

<script type="text/javascript">
/* $("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10}); */
$("img.lazy").lazyload({event : "sporty", threshold : 200, effect : "fadeIn", failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty"); }, 1000);
});

</script>