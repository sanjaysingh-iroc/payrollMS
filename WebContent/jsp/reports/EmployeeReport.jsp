<%@page import="java.io.File"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    UtilityFunctions uF=new UtilityFunctions();
    /* EncryptionUtility eU = new EncryptionUtility(); */
    
    String fromPage = (String)request.getAttribute("fromPage");
    Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
    Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
    String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    
    String isEmpLimit = (String)request.getAttribute("isEmpLimit");
    String strEmpLimit = (String)request.getAttribute("strEmpLimit");
%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript">
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
    function show_empList() {
    	dojo.event.topic.publish("show_empList"); 
    }
     
    function callMe() {  
    	window.location='AddEmployeeMode.action';
    }
    
    function showAddEmpModes(value) {
    	//alert("value == >"+value);
    	var isEmpLimit = document.getElementById("isEmpLimit").value;
    	var strEmpLimit = document.getElementById("strEmpLimit").value;
    	if(isEmpLimit == 'true') {
    		alert('You have exceeded your employee limit of '+strEmpLimit+'. Please contact  accounts@workrig.com to add additional slab for continued uasage.');
		} else {
	    	if(value == 1) {
	    		window.location='AddEmployeeInOneStep.action';
	    	} else if(value == 2) {
	    		window.location='AddEmployee.action';
	    	} else if(value == 3) {
	    		 var strAction = "AddEmployeeMode.action?fromPage=P&mode=3";
	    		 var dialogEdit = '.modal-body';
				 $(dialogEdit).empty();
				 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				 $("#modalInfo").show();
				 $(".modal-title").html('Add Employee');
				 if($(window).width() >= 700) {
					 $(".modal-dialog").width(700);
				 }
				 $.ajax({
		                url : strAction,
		                cache : false,
		                success : function(data) {
		                	//alert("data==>"+data);
		                	$(dialogEdit).html(data);  		                	
		                }
		            });
	    	} else if(value == 4) {
	    		 window.location = "AddEmployeeMode.action?mode=4";
	    	}
		}
    }
</script>
<s:if test="page=='Live'">
	<script type="text/javascript" charset="utf-8">
        $(document).ready( function () {
        	var sbStatusList = '<%=((String) request.getAttribute("sbStatusList"))%>';
       		$("select[name='f_education']").multiselect().multiselectfilter();
		});
        
		function showattribList(check) {
			if (check === true) {
				document.getElementById("advanceFilterDiv").style.display = "block";
				$("select[name='f_education']").multiselect().multiselectfilter();
			} else {
				if (document.getElementById("advanceFilterDiv")) {
					document.getElementById("advanceFilterDiv").style.display = "none";
				}
			}
		}
	</script>

</s:if>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$('#lt').DataTable({
			"order" : [],
			"columnDefs" : [ {
				"targets" : 'no-sort',
				"orderable" : false
			} ],
			'dom' : 'lBfrtip',
			'buttons' : [ 'copy', 'csv', 'excel', 'pdf', 'print' ],
			drawCallback: function(){
		        $("img.lazy").lazyload();
		   }
		});

		$("#strBirthStartDate").datepicker({
			format : 'dd/mm/yyyy',
			autoclose : true
		}).on('changeDate', function(selected) {
			var minDate = new Date(selected.date.valueOf());
			$('#strBirthEndDate').datepicker('setStartDate', minDate);
		});
		
		$("#strBirthEndDate").datepicker({
			format : 'dd/mm/yyyy',
			autoclose : true
		}).on('changeDate', function(selected) {
			var minDate = new Date(selected.date.valueOf());
			$('#strBirthStartDate').datepicker('setEndDate', minDate);
		});
		$("#strJoiningStartDate").datepicker({
			format : 'dd/mm/yyyy',
			autoclose : true
		}).on('changeDate', function(selected) {
			var minDate = new Date(selected.date.valueOf());
			$('#strJoiningEndDate').datepicker('setStartDate', minDate);
		});
		$("#strJoiningEndDate").datepicker({
			format : 'dd/mm/yyyy',
			autoclose : true
		}).on('changeDate', function(selected) {
			var minDate = new Date(selected.date.valueOf());
			$('#strJoiningStartDate').datepicker('setEndDate', minDate);
		});
		$("#strTerminateStartDate").datepicker({
			format : 'dd/mm/yyyy',
			autoclose : true
		}).on('changeDate', function(selected) {
			var minDate = new Date(selected.date.valueOf());
			$('#strTerminateEndDate').datepicker('setStartDate', minDate);
		});
		$("#strTerminateEndDate").datepicker({
			format : 'dd/mm/yyyy',
			autoclose : true
		}).on('changeDate', function(selected) {
			var minDate = new Date(selected.date.valueOf());
			$('#strTerminateStartDate').datepicker('setEndDate', minDate);
		});

		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter();
		$("#f_status").multiselect().multiselectfilter();
		$("#f_grade").multiselect().multiselectfilter();
		$("#f_employeType").multiselect().multiselectfilter();
		
	});

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
	
	function getSelectedValue(selectId) {
		var choice = document.getElementById(selectId);
		var exportchoice = "";
		for ( var i = 0, j = 0; i < choice.options.length; i++) {
			var value = choice.options[i].value;
			if(choice.options[i].selected == true && value != "") {
				
				if (j == 0) {
					exportchoice = "," + choice.options[i].value + ",";
					j++;
				} else {
					exportchoice += choice.options[i].value + ",";
					j++;
				}
			}else if(choice.options[i].selected == true && value == ""){
				exportchoice = "";
				break;
			}
		}
		return exportchoice;
	}
	
	
	function submitForm(type, strAction) {
		var org = document.getElementById("f_org").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var strGrade = getSelectedValue("f_grade");
		var strEmployeType = getSelectedValue("f_employeType");
		var status = "";
		var fromDate,toDate,empEducationType;
		if (document.getElementById("f_status")) {
			status = getSelectedValue("f_status");
		}
		var advanceFilter = "";
		var afParam = "";
		var paramValues = "";
		if (document.getElementById("advanceFilter")) {
			advanceFilter = getCheckedValue("advanceFilter");
			afParam = getCheckedValue("afParam");
			switch(afParam) {
			case "1": 
				fromDate = $("#strBirthStartDate").val();
				toDate = $("#strBirthEndDate").val();
				paramValues ='&fromDate='+fromDate+'&toDate='+toDate;
				break;
			case "2":
				fromDate =$("#strJoiningStartDate").val();
				toDate = $("#strJoiningEndDate").val();
				paramValues ='&fromDate='+fromDate+'&toDate='+toDate;
				break;
			case "3":
				fromDate =$("#strTerminateStartDate").val();
				toDate = $("#strTerminateEndDate").val();
				paramValues ='&fromDate='+fromDate+'&toDate='+toDate;
				break;
			case "4":
				 if(document.getElementById("f_education")){
					 empEducationType = getSelectedValue("f_education");
					} 
				 paramValues ='&strEducation='+empEducationType;
				break;
			}
		}
	
		if (type == '2') {
			paramValues = '&strLocation=' + location + '&strDepartment='
					+ department + '&strSbu=' + service + '&strLevel=' + level
					+ '&strStatus=' + status + '&advanceFilter='
					+ advanceFilter + '&afParam=' + afParam + paramValues+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType;
		}
		$("#divResult").html(
				'<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url : strAction + '.action?f_org=' + org + paramValues,
			data : $("#" + this.id).serialize(),
			success : function(result) {
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

	function getCheckedValue(advanceFilter) {
		var strFilter = document.getElementsByName(advanceFilter);
		var exportchoice = "";
		for ( var i = 0, j = 0; i < strFilter.length; i++) {
			if (strFilter[i].checked) {
				if (j == 0) {
					exportchoice = strFilter[i].value;
					j++;
				} else {
					exportchoice += "," + strFilter[i].value;
					j++;
				}
			}
		}
		return exportchoice;
	}
	

	function getViewData(type, event) {
		$(".view").removeClass("active");
		//$(event.target).parent().addClass("active");
		if (type === "list") {
			var action = "EmployeeReport.action";
			$("#btnList").addClass("active");
		} else if (type === "grid") {
			var action = "SearchEmployee.action?strFirstName=";
			$("#btnGrid").addClass("active");
		} else if (type === "org") {
			var action = "OrganisationalChart.action?fromPage=TS&divResult=changeViewDiv";
			$("#btnOrg").addClass("active");
		}
		$("#changeViewDiv").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'GET',
			url : action,
			success : function(result) {
				$("#changeViewDiv").html(result);
				if (type === "list") {
					$(".viewChangeButtons")[1].remove();
				}
			}
		});
	}
</script>

<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
		<%
		session.removeAttribute(IConstants.MESSAGE); 
		String strMessage = (String)request.getAttribute(IConstants.MESSAGE);
		if(strMessage == null) {
			strMessage = "";
		} %>
		<%=strMessage %>
	
	<div style="float: left; width: 98%; margin: 0px 0px; padding: 0px;">
		<%=uF.showData((String)request.getAttribute("sbMessage"), "") %>
	</div>
	
	<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
	<s:if test="page=='Live'">
		<div class="btn-group viewChangeButtons" data-toggle="btn-toggle">
			<button id="btnList" type="button" class="btn btn-default btn-sm view active" onclick="getViewData('list',event)" 
				data-placement="bottom" title="" data-toggle="tooltip" for="List View" data-original-title="List View">
				<i class="fa fa-bars" aria-hidden="true"></i>
			</button>
			<button id="btnGrid" type="button" class="btn btn-default btn-sm view" onclick="getViewData('grid',event)"
				data-placement="bottom" title="" data-toggle="tooltip" for="Grid View" data-original-title="Grid View">
				<i class="fa fa-th" aria-hidden="true"></i>
			</button>
			<button id="btnOrg" type="button" class="btn btn-default btn-sm view" onclick="getViewData('org',event)"
				data-placement="bottom" title="" data-toggle="tooltip" for="Chart View" data-original-title="Chart View">
				<i class="fa fa-sitemap" aria-hidden="true"></i>
			</button>
		</div>
	</s:if>
	<% } %>

	<div id="changeViewDiv" class="margintop20">
		<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
		<s:if test="page=='Live'">
			<% if(uF.parseToBoolean(isEmpLimit)) { %>
				<div class="callout callout-warning" style="margin-bottom: 0px; padding: 7px 10px; font-weight: 600;">
					Dear Customer, You have exceeded your employee limit of <%=strEmpLimit %> employees. We have extended a grace period. Please contact <a href="mailto:accounts@workrig.com">accounts@workrig.com</a> to add additional slab for continued usage.
				</div>
			<% } %>
			<s:form name="frmLiveEmployee" action="EmployeeReport" theme="simple">
				<div class="box box-default" style="margin-top: 10px;"> <!--  collapsed-box -->
					<%-- <div class="box-header with-border">
						<h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
						<div class="box-tools pull-right">
							<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						</div>
					</div> --%>
					<!-- /.box-header -->
					<div class="box-body" style="padding: 5px; overflow-y: auto;"> <!--  display: none; -->
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1', 'EmployeeReport');" list="organisationList" key="" />
								</div>
								<div
									class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
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
								<%-- <div
									class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level"
										listKey="levelId" listValue="levelCodeName" multiple="true"
										list="levelList" key="" />
								</div> --%>
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
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Status</p>
									<s:select theme="simple" name="f_status" id="f_status" listKey="statusId" listValue="statusName" list="empStatusList" key="" multiple="true" />
								</div>
							</div>
						</div>
						<br>
						<div class="row row_without_margin">
							<div class="col-lg-12 col-md-12 col-sm-12 autoWidth paddingright0">
								<p style="padding-left: 5px;">
									Advanced Filter&nbsp;<input type="checkbox" name="advanceFilter" id="advanceFilter" value="AF"
										<%=(request.getAttribute("advanceFilter") != null && !((String)request.getAttribute("advanceFilter")).equals("")) ? "checked" : ""%>
										onclick="showattribList(this.checked);" />
								</p>
							</div>
						</div>
						<div class="filter_div">
							<div class="row row_without_margin">
								<div id="advanceFilterDiv" style="<%=(request.getAttribute("advanceFilter") != null && !((String)request.getAttribute("advanceFilter")).equals("")) ? "display: block;" : "display: none;"%> width: 100%;"
									class="col-lg-4 col-md-6 col-sm-12">
									<table class="table table-bordered" style="clear: both;">
										<tr>
											<td><s:radio name="afParam" list="#{'1':'Birth Date'}" /></td>
											<td><s:textfield name="strBirthStartDate" id="strBirthStartDate" cssStyle="width: 95px !important;" cssClass="form-control autoWidth inline"></s:textfield>&nbsp; 
												<s:textfield name="strBirthEndDate" id="strBirthEndDate" cssStyle="width: 95px !important;" cssClass="form-control autoWidth inline"></s:textfield></td>
										</tr>
										<tr>
											<td><s:radio name="afParam" list="#{'2':'Joining Date'}" />
											</td>
											<td><s:textfield name="strJoiningStartDate" id="strJoiningStartDate" cssStyle="width: 95px !important;" cssClass="form-control autoWidth inline"></s:textfield>&nbsp; 
												<s:textfield name="strJoiningEndDate" id="strJoiningEndDate" cssStyle="width: 95px !important;" cssClass="form-control autoWidth inline"></s:textfield></td>
										</tr>
										<tr>
											<td><s:radio name="afParam" list="#{'3':'Terminated'}" />
											</td>
											<td><s:textfield name="strTerminateStartDate" id="strTerminateStartDate" cssStyle="width: 95px !important;" cssClass="form-control autoWidth inline"></s:textfield>&nbsp; 
												<s:textfield name="strTerminateEndDate" id="strTerminateEndDate" cssStyle="width: 95px !important;" cssClass="form-control autoWidth inline"></s:textfield></td>
										</tr>
										<tr>
											<td valign="top"><s:radio name="afParam" list="#{'4':'Education'}" /></td>
											<td><s:select theme="simple" name="f_education" id="f_education" listKey="eduId" listValue="eduName" headerKey="" multiple="true" size="4" list="eduList" key="" required="true" /></td>
										</tr>
									</table>
								</div>
							</div>
						</div>
						<div class="row row_without_margin">
							<div class="col-lg-12 col-md-12 col-sm-12 autoWidth paddingright0">
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2', 'EmployeeReport');" />
							</div>
						</div>
					</div>
				</div>
			</s:form>
		</s:if>
		<s:elseif test="page=='Pending'">
			<s:form name="frmPendingEmployee" action="PendingEmployeeReport"
				theme="simple">
				<div class="box box-default" style="margin-top: 10px;"> <!--  collapsed-box -->
					<%-- <div class="box-header with-border">
						<h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
						<div class="box-tools pull-right">
							<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						</div>
					</div> --%>
					<div class="box-body" style="padding: 5px; overflow-y: auto;"> <!--  display: none; -->
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" headerKey="" headerValue="All Organisations" onchange="submitForm('1', 'PendingEmployeeReport');" list="organisationList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Service</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
								</div>
								<%-- <div
									class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level"
										listKey="levelId" listValue="levelCodeName" list="levelList"
										key="" multiple="true" />
								</div> --%>
								
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
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2', 'PendingEmployeeReport');" />
								</div>
							</div>
						</div>
						<br>
					</div>
				</div>
			</s:form>
		</s:elseif>
		<s:else>
			<s:form name="frmExEmployee" action="ExEmployeeReport" theme="simple">
				<div class="box box-default" style="margin-top: -20px;"> <!--  collapsed-box -->
					<%-- <div class="box-header with-border">
						<h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
						<div class="box-tools pull-right">
							<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						</div>
					</div> --%>
					<div class="box-body" style="padding: 5px; overflow-y: auto;"><!--  display: none; -->
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1', 'ExEmployeeReport');" list="organisationList" key="" />
								</div>
								<div
									class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
								</div>
								<div
									class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
								</div>
								<div
									class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Service</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
								</div>
								<%-- <div
									class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level"
										listKey="levelId" listValue="levelCodeName" list="levelList"
										key="" multiple="true" />
								</div> --%>
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
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
								</div>
								<div
									class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2', 'ExEmployeeReport');" />
								</div>
							</div>
						</div>
						<br>
					</div>
				</div>
			</s:form>
		</s:else>
		<%} %>
		<!-- Place holder where add and delete buttons will be generated -->
		<!-- <div class="add_delete_toolbar"></div> -->
		<s:property value="message" />
		<s:if test="page=='Live'">
			<script>
				function selectall(x, strEmpId) {
					var status = x.checked;
					var arr = document.getElementsByName(strEmpId);
					for (i = 0; i < arr.length; i++) {
						arr[i].checked = status;
					}
				}
			</script>
			<s:form theme="simple" action="EmployeeBulkActivity" method="POST">
				<% if (strUserType != null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || 
                      strUserType.equalsIgnoreCase(IConstants.CEO) || strUserType.equalsIgnoreCase(IConstants.CFO) || strUserType.equalsIgnoreCase(IConstants.RECRUITER))) {
                %>
				<div class="row row_without_margin">
					<div class="col-lg-6 col-md-6 col-sm-6 autoWidth">
						<input type="hidden" name="isEmpLimit" id="isEmpLimit" value="<%=isEmpLimit %>" />
						<input type="hidden" name="strEmpLimit" id="strEmpLimit" value="<%=strEmpLimit %>" />
						<select name="addEmpModes" id="addEmpModes" class="validateRequired" onchange="showAddEmpModes(this.value);">
							<option value="">Select Mode to Add Employee</option>
							<option value="1">Add New Employee(1 Step)</option>
							<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_ADD_EMPLOYEE_8_STEP)) && hmFeatureUserTypeId.get(IConstants.F_ADD_EMPLOYEE_8_STEP).contains(strUsertypeId)) { %>
							<option value="2">Add New Employee(8 Step)</option>
							<% } %>
							<option value="3">Let Employee Enter Info</option>
							<option value="4">Bulk Import</option>
						</select>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-6 autoWidth" style="float: right;">
						<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_BULK_EMPLOYEE_ACTIVITY)) && hmFeatureUserTypeId.get(IConstants.F_BULK_EMPLOYEE_ACTIVITY).contains(strUsertypeId)) { %>
						<p style="margin-bottom: 15px; float: right;"> <input type="submit" value="Bulk Employee Activity" class="btn btn-primary" /></p>
						<% } %>
					</div>
				</div>
				<% } %>

				<table id="lt" class="table table-bordered" style="width: 100%; margin-top: 30px; clear: both;">
					<thead>
						<tr>
							<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
							<th align="left" class="no-sort"><input type="checkbox" onclick="selectall(this, 'strIsAssigActivity')" /></th>
							<%} %>
							<th style="text-align: left;">Employee Name</th>
							<th style="text-align: left;">Emp Code</th>
							<th style="text-align: left;">Joining Date</th>
							<th style="text-align: left;">Designation</th>
							<th style="text-align: left;">Department</th>
							<th style="text-align: left;">Work Location</th>
							<th style="text-align: left;">Employee Status</th>
							<th style="text-align: left;">Employment Type</th>
							<s:if test="advanceFilter!=null && advanceFilter=='AF'">
								<% String af=(String)request.getAttribute("afParam");
                                    if(af!=null && af.equals("1")) { %>
								<th style="text-align: left;">Birth Date</th>
								<% } else if(af!=null && af.equals("3")) { %>
								<th style="text-align: left;">Terminate Date</th>
								<% } else if(af!=null && af.equals("4")) { %>
								<th style="text-align: left;">Education</th>
								<% } %>
							</s:if>
							<th style="text-align: left;" class="no-sort">Facts</th>
						</tr>
					</thead>
					<tbody>
						<%
                            java.util.List couterlist = (java.util.List) request.getAttribute("reportList");
                            Map<String, String> hmEducation=(Map<String, String>)request.getAttribute("hmEducation");
                            if(hmEducation==null) hmEducation=new HashMap<String, String>();
                           	for (int i = 0; couterlist != null && i < couterlist.size(); i++) {
                           		java.util.List cinnerlist = (java.util.List) couterlist.get(i);
						%>
						<tr id=<%=cinnerlist.get(0)%>>
							<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
								<td><input type="checkbox" name="strIsAssigActivity" value="<%=cinnerlist.get(0)%>" /></td>
							<%} %>
							<td>
							
								<% if(docRetriveLocation==null) { %>
									<% File file = new File(IConstants.DOCUMENT_LOCATION + cinnerlist.get(13));
										boolean existFile = false;
										if(file.exists()){
											existFile = true;
											//System.out.println("emp_id==>"+cinnerlist.get(0)+"---image===>"+cinnerlist.get(13));
										} 
									%>
									<%-- <img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(13) %>"> --%>
									<%-- <%if(cinnerlist.get(13)!=null && !cinnerlist.get(13).equals("") && !cinnerlist.get(13).toString().contains("avatar_photo.png")){ %> --%>
									<%if(existFile){ %>
										<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(13) %>" data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(13) %>">
									<% } else{ %>
										<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(13) %>">
									<% } %>
								<% } else { %>		
								<% File file = new File(docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(13));
									boolean existFile = false;
									if(file.exists()){
										existFile = true;
										///System.out.println("emp_id==>"+cinnerlist.get(0)+"---image===>"+cinnerlist.get(13));
									} 
								%>						
									<%-- <img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(13) %>" > --%>
									<%-- <%if(cinnerlist.get(13)!=null && !cinnerlist.get(13).equals("") && !cinnerlist.get(13).toString().contains("avatar_photo.png")){ %> --%>
									<%if(existFile){ %>
										<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(13) %>" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(13) %>">
									<% } else{ %>
										<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(13) %>" >
									<% } %>
								<% } %> <%=cinnerlist.get(2)%></td>
							<td><%=cinnerlist.get(1)%></td>
							<td><%=cinnerlist.get(5)%></td>
							<td><%=cinnerlist.get(6)%></td>
							<td><%=cinnerlist.get(7)%></td>
							<td><%=cinnerlist.get(8)%></td>
							<td><%=cinnerlist.get(9)%></td>
							<td><%=cinnerlist.get(10)%></td>
							<s:if test="advanceFilter!=null && advanceFilter=='AF'">
								<%	String af=(String)request.getAttribute("afParam");
									if(af!=null && af.equals("1")) { %>
								<td><%=cinnerlist.get(12)%></td>
								<% } else if(af!=null && af.equals("3")) { %>
								<td><%=cinnerlist.get(12)%></td>
								<% } else if(af!=null && af.equals("4")) { %>
								<td><%=uF.showData(hmEducation.get(""+cinnerlist.get(0)),"") %></td>
								<% } %>
							</s:if>
							<td><%=cinnerlist.get(11)%></td>
							<%-- <%System.out.println(cinnerlist.get(11)); %> --%>
						</tr>
						<% } %>
					</tbody>
				</table>
			</s:form>
		</s:if>
		
		<s:elseif test="page=='Pending'">
			<div style="clear: both; padding-top: 20px;"></div>
			<table id="lt" class="table table-bordered" style="width: 100%; margin-top: 30px; clear: both;">
				<thead>
					<tr>
						<th style="text-align: left;">Employee Name</th>
						<th style="text-align: left;">Personal Email Id</th>
						<th style="text-align: left;">Mobile No</th>
						<th style="text-align: left;" class="no-sort">Facts</th>
					</tr>
				</thead>
				<tbody>
					<%
                       	java.util.List couterlist = (java.util.List) request.getAttribute("reportList");
                       	for (int i = 0; couterlist != null && i < couterlist.size(); i++) {
							java.util.List cinnerlist = (java.util.List) couterlist.get(i);
                       %>
					<tr id=<%=cinnerlist.get(0)%>>
						<td>
							<%if(docRetriveLocation==null) { %> 
							<%-- <img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png"
								data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(7) %>"> --%>
								<% File file = new File(IConstants.DOCUMENT_LOCATION + cinnerlist.get(7));
									boolean existFile = false;
									if(file.exists()){
										existFile = true;
									} 
								%>
								
								<%if(cinnerlist.get(7)!=null && !cinnerlist.get(7).equals("") && !cinnerlist.get(7).toString().contains("avatar_photo.png")){ %>
									<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(7) %>"
										data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(7) %>">
								<%} else{ %>
									<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png"
										data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(7) %>">
								<%} %>
							<% } else { %> 
							<%-- <img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png"
								data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(7) %>"> --%>
							<% File file = new File(docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(7));
								boolean existFile = false;
								if(file.exists()){
									existFile = true;
								} 
							%>
							
							<%-- <%if(cinnerlist.get(7)!=null && !cinnerlist.get(7).equals("") && !cinnerlist.get(7).toString().contains("avatar_photo.png")){ %> --%>
							<%if(existFile){ %>
								<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(7) %>"
									data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(7) %>">
							<%} else{ %>
								<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png"
									data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(7) %>">
							<%} %>
							
							<% } %> <%=cinnerlist.get(1)%></td>
						<td><%=cinnerlist.get(4)%></td>
						<td><%=cinnerlist.get(5)%></td>
						<td><%=cinnerlist.get(6)%></td>
					</tr>
					<% } %>
				</tbody>
			</table>
			<div class="fieldset">
				<fieldset>
					<legend>Status</legend>
					<div>
						<img border="0" src="images1/factsheet_live.png" style="padding: 5px 5px 0 5px;">Employee Filled Onboarding Form
					</div>
					<div>
						<img border="0" src="images1/factsheet_cancel.png" style="padding: 5px 5px 0 5px;">Employee did not Fill Onboarding Form
					</div>
				</fieldset>
			</div>
		</s:elseif>
		<s:else>
			<div style="clear: both; padding-top: 20px;"></div>
			<table id="lt" class="table table-bordered" style="width: 100%; margin-top: 30px;">
				<thead>
					<tr>
						<th style="text-align: left;">Employee Name</th>
						<th style="text-align: left;">Personal Email Id</th>
						<th style="text-align: left;">Mobile No</th>
						<th style="text-align: left;" class="no-sort">Facts</th>
						<th style="text-align: left;" class="no-sort">Rejoin</th>
					</tr>
				</thead>
				<tbody>
					<%
                        java.util.List couterlist = (java.util.List) request.getAttribute("reportList");
                        for (int i = 0; couterlist != null && i < couterlist.size(); i++) {
                        java.util.List cinnerlist = (java.util.List) couterlist.get(i);
                    %>
					<tr id=<%=cinnerlist.get(0)%>>
						<td>
							<%if(docRetriveLocation==null) { %> 
							<%-- <img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png"
								data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(7) %>"> --%>
								<% File file = new File(IConstants.DOCUMENT_LOCATION + cinnerlist.get(7));
									boolean existFile = false;
									if(file.exists()){
										existFile = true;
									} 
								%>
								
								<%-- <%if(cinnerlist.get(7)!=null && !cinnerlist.get(7).equals("") && !cinnerlist.get(7).toString().contains("avatar_photo.png")){ %> --%>
								<%if(existFile){ %>
									<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(7) %>"
										data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(7) %>">
								<%} else{ %>
									<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png"
										data-original="<%=IConstants.DOCUMENT_LOCATION + cinnerlist.get(7) %>">
								<%} %>
							
							<% } else { %>
							<%-- <img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png"
								data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(7) %>"> --%>
							
								<% File file = new File(docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(7));
									boolean existFile = false;
									if(file.exists()){
										existFile = true;
									} 
								%>
							<%-- <%if(cinnerlist.get(7)!=null && !cinnerlist.get(7).equals("") && !cinnerlist.get(7).toString().contains("avatar_photo.png")){ %> --%>
							<%if(existFile){ %>
								<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(7) %>"
									data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(7) %>">
							<%} else{ %>
								<img height="22" width="22" border="0" class="lazy img-circle" style="float: left; margin-right: 5px; border: 1px solid #CCCCCC;" src="userImages/avatar_photo.png"
									data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_22x22+"/"+cinnerlist.get(7) %>">
							<%} %>
							<% } %> <%=cinnerlist.get(1)%></td>
						<td><%=cinnerlist.get(4)%></td>
						<td><%=cinnerlist.get(5)%></td>
						<td><%=cinnerlist.get(6)%></td>
						<td><a href="javascript:void(0)" onclick="(confirm('Are you sure you want to rejoin <%=cinnerlist.get(1)%> <%=cinnerlist.get(3)%>?') ? window.location='ReJoinEmployee.action?strEmpId=<%=(String)cinnerlist.get(0) %>' : '')">Rejoin</a>
						</td>
					</tr>
					<% } %>
				</tbody>
			</table>
		</s:else>
	</div>
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


<script>
	$("img.lazy").lazyload({
		threshold : 200,
		effect : "fadeIn",
		failure_limit : 10
	});
</script>

